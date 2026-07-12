package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.LogEntry
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class GetAuditLogsUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    operator fun invoke(): Flow<List<LogEntry>> {
        return combine(
            repository.getAllAuditLogs(),
            repository.getAllProposals()
        ) { auditLogs, proposals ->
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale("id", "ID"))
            val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))

            auditLogs.map { log ->
                val instant = Instant.ofEpochMilli(log.timestamp)
                val zoneId = ZoneId.systemDefault()
                val logDate = instant.atZone(zoneId).toLocalDate()
                val logTime = instant.atZone(zoneId).toLocalTime()

                val timeStr = logTime.format(timeFormatter)
                val dateGroup = when (logDate) {
                    today -> "Hari Ini"
                    yesterday -> "Kemarin"
                    else -> logDate.format(dateFormatter)
                }

                // Resolve related berkas
                val proposal = proposals.firstOrNull { it.id == log.proposalId }
                val relatedBerkas = proposal?.nomorBerkas

                // Map actorId directly
                val person = log.actorId.takeIf { it.isNotBlank() } ?: "Sistem"
                val role = if (log.actorId.isBlank() || log.actorId.lowercase(Locale.ROOT) == "sistem") "System" else "Operator"

                // Resolve Category and Title based on Action/newStatus
                val (categoryName, title) = when (log.action) {
                    "PROPOSAL_CREATED", "CREATE_PROPOSAL" -> {
                        Pair("Berkas", "Berkas baru dibuat")
                    }
                    "CANCEL_PROPOSAL" -> {
                        Pair("Berkas", "Berkas dikembalikan untuk revisi")
                    }
                    "UPDATE_PROPOSAL_STATUS" -> {
                        when (log.newStatus) {
                            "VERIFIED" -> Pair("Penilaian", "Tim Penilai menyetujui usul")
                            "APPROVED" -> Pair("Disetujui", "Bupati menyetujui pemusnahan")
                            "REJECTED" -> Pair("Ditolak", "Tim Penilai menolak usul")
                            "DISPOSED" -> Pair("Pemusnahan", "Pemusnahan arsip dilaksanakan")
                            else -> Pair("Berkas", "Status berkas diperbarui: ${log.newStatus}")
                        }
                    }
                    "CREATE_BERITA_ACARA" -> {
                        Pair("Berita Acara", "Berita Acara dibuat")
                    }
                    "DISPOSE_PROPOSAL" -> {
                        Pair("Pemusnahan", "Pemusnahan selesai dilaksanakan")
                    }
                    "ARCHIVE_DISPOSED" -> {
                        Pair("Pemusnahan", "Arsip dimusnahkan secara fisik")
                    }
                    "EXPORT" -> {
                        Pair("Ekspor", "PDF diunduh")
                    }
                    else -> {
                        val formattedAction = log.action.replace("_", " ").lowercase(Locale.ROOT)
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                        Pair("Sistem", formattedAction)
                    }
                }

                LogEntry(
                    id = log.id,
                    time = timeStr,
                    dateGroup = dateGroup,
                    sortKey = log.timestamp,
                    categoryName = categoryName,
                    title = title,
                    description = log.notes ?: "Aktivitas tercatat",
                    relatedBerkas = relatedBerkas,
                    person = person,
                    role = role,
                    ipAddress = "Sistem Internal"
                )
            }
        }
    }
}
