package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.AuditLog
import com.bpkpad.siarsip.feature.arsip.domain.model.BerkasUsulMusnah
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

enum class StageStatus { DONE, ACTIVE, PENDING, REJECTED }

data class TrackingStage(
    val name: String,
    val description: String,
    val person: String,
    val date: String,
    val status: StageStatus
)

data class TrackingBerkas(
    val nomor: String,
    val perihal: String,
    val sumber: String,
    val arsipCount: Int,
    val stages: List<TrackingStage>,
    val proposalId: String
) {
    val currentStageIndex: Int
        get() = stages.indexOfFirst { it.status == StageStatus.ACTIVE }
            .takeIf { it >= 0 } ?: stages.indexOfLast { it.status == StageStatus.DONE }

    val overallStatus: String
        get() = when {
            stages.any { it.status == StageStatus.REJECTED } -> "Ditolak"
            stages.all { it.status == StageStatus.DONE }     -> "Selesai"
            stages.any { it.status == StageStatus.ACTIVE }   -> "Diproses"
            else                                              -> "Diajukan"
        }
}

class GetTrackingInfoUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    operator fun invoke(): Flow<List<TrackingBerkas>> {
        return combine(
            repository.getAllProposals(),
            repository.getAllAuditLogs()
        ) { proposals, auditLogs ->
            proposals.map { proposal ->
                val proposalLogs = auditLogs.filter { it.proposalId == proposal.id }
                
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                fun formatTime(timestamp: Long): String = dateFormat.format(Date(timestamp))
                
                // Fallback helper for actorId (Amendment 3)
                fun getActorName(log: AuditLog?, defaultName: String): String {
                    if (log == null) return defaultName
                    return log.actorId ?: "Sistem BPKPAD"
                }

                // Stage 1: Berkas Dibuat
                val createdLog = proposalLogs.find { it.action == "PROPOSAL_CREATED" }
                val createdStage = TrackingStage(
                    name = "Berkas Dibuat",
                    description = "Draft berkas usul musnah diinput",
                    person = getActorName(createdLog, "Sistem BPKPAD"),
                    date = createdLog?.timestamp?.let { formatTime(it) } ?: "—",
                    status = StageStatus.DONE
                )

                // Stage 2: Diajukan ke Tim Penilai
                val proposedStage = TrackingStage(
                    name = "Diajukan ke Tim Penilai",
                    description = "Berkas dikirim untuk dinilai",
                    person = getActorName(createdLog, "Sistem BPKPAD"),
                    date = createdLog?.timestamp?.let { formatTime(it) } ?: "—",
                    status = StageStatus.DONE
                )

                // Stage 3: Penilaian Tim (VERIFIED state)
                val verifiedLog = proposalLogs.find { it.action == "UPDATE_PROPOSAL_STATUS" && it.newStatus == "VERIFIED" }
                val verifiedStage = when (proposal.status) {
                    "PROPOSED" -> TrackingStage(
                        name = "Penilaian Tim",
                        description = "Sedang dinilai oleh Tim Penilai",
                        person = "Tim Penilai BPKPAD",
                        date = "—",
                        status = StageStatus.ACTIVE
                    )
                    "VERIFIED", "APPROVED", "DISPOSED" -> {
                        val desc = if (proposal.suratPertimbanganNomor != null) {
                            "Disetujui Tim Penilai (Surat Pertimbangan No: ${proposal.suratPertimbanganNomor})"
                        } else {
                            "Tim Penilai setuju usul musnah"
                        }
                        TrackingStage(
                            name = "Penilaian Tim",
                            description = desc,
                            person = getActorName(verifiedLog, "Tim Penilai BPKPAD"),
                            date = verifiedLog?.timestamp?.let { formatTime(it) } ?: "—",
                            status = StageStatus.DONE
                        )
                    }
                    else -> TrackingStage(
                        name = "Penilaian Tim",
                        description = "Sedang dinilai oleh Tim Penilai",
                        person = "Tim Penilai BPKPAD",
                        date = "—",
                        status = StageStatus.PENDING
                    )
                }

                // Stage 4: Pengiriman ke Kepala Daerah (APPROVED state)
                val approvedLog = proposalLogs.find { it.action == "UPDATE_PROPOSAL_STATUS" && it.newStatus == "APPROVED" }
                val approvedStage = when (proposal.status) {
                    "PROPOSED" -> TrackingStage(
                        name = "Pengiriman ke Kepala Daerah",
                        description = "Menunggu persetujuan Bupati/ANRI",
                        person = "—",
                        date = "—",
                        status = StageStatus.PENDING
                    )
                    "VERIFIED" -> TrackingStage(
                        name = "Pengiriman ke Kepala Daerah",
                        description = "Menunggu persetujuan Bupati/ANRI",
                        person = "Bupati Balangan",
                        date = "—",
                        status = StageStatus.ACTIVE
                    )
                    "APPROVED", "DISPOSED" -> {
                        val approver = proposal.jenisPersetujuanAkhir ?: "Bupati"
                        val desc = if (proposal.nomorPersetujuanAkhir != null) {
                            "Disetujui oleh $approver (Surat No: ${proposal.nomorPersetujuanAkhir})"
                        } else {
                            "Disetujui oleh $approver"
                        }
                        TrackingStage(
                            name = "Pengiriman ke Kepala Daerah",
                            description = desc,
                            person = getActorName(approvedLog, approver),
                            date = approvedLog?.timestamp?.let { formatTime(it) } ?: "—",
                            status = StageStatus.DONE
                        )
                    }
                    else -> TrackingStage(
                        name = "Pengiriman ke Kepala Daerah",
                        description = "Menunggu persetujuan Bupati/ANRI",
                        person = "—",
                        date = "—",
                        status = StageStatus.PENDING
                    )
                }

                // Stage 5: Pemusnahan & Berita Acara (DISPOSED state)
                val disposedLog = proposalLogs.find { it.action == "DISPOSE_PROPOSAL" || it.action == "CREATE_BERITA_ACARA" }
                val disposedStage = when (proposal.status) {
                    "PROPOSED", "VERIFIED" -> TrackingStage(
                        name = "Pemusnahan & Berita Acara",
                        description = "Pelaksanaan pemusnahan arsip",
                        person = "—",
                        date = "—",
                        status = StageStatus.PENDING
                    )
                    "APPROVED" -> TrackingStage(
                        name = "Pemusnahan & Berita Acara",
                        description = "Menunggu pembuatan Berita Acara",
                        person = "Tim Pemusnahan",
                        date = "—",
                        status = StageStatus.ACTIVE
                    )
                    "DISPOSED" -> TrackingStage(
                        name = "Pemusnahan & Berita Acara",
                        description = "Pemusnahan selesai dilaksanakan",
                        person = getActorName(disposedLog, "Tim Pemusnahan"),
                        date = disposedLog?.timestamp?.let { formatTime(it) } ?: "—",
                        status = StageStatus.DONE
                    )
                    else -> TrackingStage(
                        name = "Pemusnahan & Berita Acara",
                        description = "Pelaksanaan pemusnahan arsip",
                        person = "—",
                        date = "—",
                        status = StageStatus.PENDING
                    )
                }

                TrackingBerkas(
                    nomor = proposal.nomorBerkas,
                    perihal = proposal.perihal,
                    sumber = proposal.sumberModul,
                    arsipCount = proposal.archives.size,
                    stages = listOf(createdStage, proposedStage, verifiedStage, approvedStage, disposedStage),
                    proposalId = proposal.id
                )
            }
        }.distinctUntilChanged() // Distinct until changed flow optimization (Amendment 2)
    }
}
