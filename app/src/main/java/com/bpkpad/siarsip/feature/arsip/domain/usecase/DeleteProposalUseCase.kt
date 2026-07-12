package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.AuditLog
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class DeleteProposalUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    suspend operator fun invoke(proposalId: String, actorId: String): Result<Unit> {
        return try {
            val proposal = repository.getProposalById(proposalId).first()
                ?: return Result.failure(Exception("Proposal tidak ditemukan"))

            val auditLogs = mutableListOf<AuditLog>()
            
            // Add cancellation log to preserve audit history
            auditLogs.add(
                AuditLog(
                    id = UUID.randomUUID().toString(),
                    action = "CANCEL_PROPOSAL",
                    actorId = actorId,
                    archiveId = null,
                    proposalId = proposalId,
                    beritaAcaraId = null,
                    previousStatus = proposal.status,
                    newStatus = "AVAILABLE",
                    notes = "Berkas usul musnah ${proposal.nomorBerkas} dibatalkan/dikembalikan untuk revisi & penelusuran ulang.",
                    timestamp = System.currentTimeMillis()
                )
            )

            repository.deleteProposal(proposalId, auditLogs)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
