package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.AuditLog
import com.bpkpad.siarsip.feature.arsip.domain.model.BerkasUsulMusnah
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import java.util.UUID
import javax.inject.Inject

class CreateProposalUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    suspend operator fun invoke(
        nomorBerkas: String,
        tanggal: String,
        unitPengolah: String,
        sumberModul: String,
        perihal: String,
        archiveIds: List<String>,
        actorId: String
    ): Result<Unit> {
        return try {
            if (nomorBerkas.isBlank()) return Result.failure(Exception("Nomor berkas tidak boleh kosong"))
            if (archiveIds.isEmpty()) return Result.failure(Exception("Harus memilih minimal 1 arsip"))

            val proposalId = UUID.randomUUID().toString()
            val proposal = BerkasUsulMusnah(
                id = proposalId,
                nomorBerkas = nomorBerkas,
                tanggal = tanggal,
                unitPengolah = unitPengolah,
                sumberModul = sumberModul,
                perihal = perihal,
                status = "PROPOSED",
                createdAt = System.currentTimeMillis()
            )

            val auditLogs = mutableListOf<AuditLog>()

            // Log action for proposal creation
            auditLogs.add(
                AuditLog(
                    id = UUID.randomUUID().toString(),
                    action = "PROPOSAL_CREATED",
                    actorId = actorId,
                    archiveId = null,
                    proposalId = proposalId,
                    beritaAcaraId = null,
                    previousStatus = null,
                    newStatus = "PROPOSED",
                    notes = "Membuat berkas usul musnah $nomorBerkas dengan ${archiveIds.size} arsip",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Log status transitions for each archive
            for (archiveId in archiveIds) {
                auditLogs.add(
                    AuditLog(
                        id = UUID.randomUUID().toString(),
                        action = "ARCHIVE_PROPOSED",
                        actorId = actorId,
                        archiveId = archiveId,
                        proposalId = proposalId,
                        beritaAcaraId = null,
                        previousStatus = "AVAILABLE",
                        newStatus = "PROPOSED",
                        notes = "Arsip dimasukkan ke berkas usul musnah $nomorBerkas",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

            // Insert proposal and update archives with logs in one transaction
            repository.insertProposal(proposal, archiveIds, auditLogs)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
