package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.AuditLog
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class UpdateProposalStatusUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    suspend operator fun invoke(
        proposalId: String,
        newStatus: String,
        actorId: String,
        suratPertimbanganNomor: String? = null,
        suratPertimbanganPerihal: String? = null,
        jenisPersetujuanAkhir: String? = null,
        nomorPersetujuanAkhir: String? = null,
        perihalPersetujuanAkhir: String? = null
    ): Result<Unit> {
        return try {
            val proposal = repository.getProposalById(proposalId).first()
                ?: return Result.failure(Exception("Proposal tidak ditemukan"))

            val currentStatus = proposal.status

            // Validate State Machine (Rule 5)
            val isValidTransition = when (currentStatus) {
                "PROPOSED" -> newStatus == "VERIFIED"
                "VERIFIED" -> newStatus == "APPROVED"
                // APPROVED -> DISPOSED is handled only via CreateBeritaAcaraUseCase
                else -> false
            }

            if (!isValidTransition) {
                return Result.failure(Exception("Transisi status tidak valid: $currentStatus -> $newStatus"))
            }

            val auditLogs = mutableListOf<AuditLog>()
            
            // Build dynamic log notes
            val customNotes = when (newStatus) {
                "VERIFIED" -> "Disetujui Tim Penilai berdasarkan Surat Pertimbangan No: $suratPertimbanganNomor ($suratPertimbanganPerihal)"
                "APPROVED" -> "Disetujui oleh $jenisPersetujuanAkhir berdasarkan Surat No: $nomorPersetujuanAkhir ($perihalPersetujuanAkhir)"
                else -> "Mengubah status berkas usul musnah ${proposal.nomorBerkas} menjadi $newStatus"
            }

            // Log action for proposal
            auditLogs.add(
                AuditLog(
                    id = UUID.randomUUID().toString(),
                    action = "UPDATE_PROPOSAL_STATUS",
                    actorId = actorId,
                    archiveId = null,
                    proposalId = proposalId,
                    beritaAcaraId = null,
                    previousStatus = currentStatus,
                    newStatus = newStatus,
                    notes = customNotes,
                    timestamp = System.currentTimeMillis()
                )
            )

            // Log for each archive in the proposal
            val archives = repository.getArchivesByProposal(proposalId).first()
            for (archive in archives) {
                auditLogs.add(
                    AuditLog(
                        id = UUID.randomUUID().toString(),
                        action = "ARCHIVE_STATUS_TRANSITION",
                        actorId = actorId,
                        archiveId = archive.id,
                        proposalId = proposalId,
                        beritaAcaraId = null,
                        previousStatus = currentStatus,
                        newStatus = newStatus,
                        notes = "Mengubah status arsip menjadi $newStatus (melalui berkas ${proposal.nomorBerkas})",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

            // Update status and insert logs in repository transaction
            repository.updateProposalStatus(
                proposalId = proposalId,
                status = newStatus,
                auditLogs = auditLogs,
                suratPertimbanganNomor = suratPertimbanganNomor,
                suratPertimbanganPerihal = suratPertimbanganPerihal,
                jenisPersetujuanAkhir = jenisPersetujuanAkhir,
                nomorPersetujuanAkhir = nomorPersetujuanAkhir,
                perihalPersetujuanAkhir = perihalPersetujuanAkhir
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
