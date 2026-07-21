package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.AuditLog
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class RemoveArchiveFromProposalUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    suspend operator fun invoke(proposalId: String, archiveId: String, actorId: String): Result<Unit> {
        return try {
            val proposal = repository.getProposalById(proposalId).first()
                ?: return Result.failure(Exception("Proposal tidak ditemukan"))
            
            val archive = repository.getAllArchives().first().firstOrNull { it.id == archiveId }
                ?: return Result.failure(Exception("Arsip tidak ditemukan"))

            val auditLog = AuditLog(
                id = UUID.randomUUID().toString(),
                action = "REMOVE_ARCHIVE_FROM_PROPOSAL",
                actorId = actorId,
                archiveId = archiveId,
                proposalId = proposalId,
                beritaAcaraId = null,
                previousStatus = archive.status,
                newStatus = "AVAILABLE",
                notes = "Arsip ${archive.fullKode} dikeluarkan dari berkas usul musnah ${proposal.nomorBerkas} saat revisi.",
                timestamp = System.currentTimeMillis()
            )

            repository.removeArchiveFromProposal(archiveId, auditLog)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
