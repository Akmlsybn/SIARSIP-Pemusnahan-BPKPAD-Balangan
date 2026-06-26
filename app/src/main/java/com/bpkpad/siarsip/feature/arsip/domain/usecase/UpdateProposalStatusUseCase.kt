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
        actorId: String
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

            // Update status in repo
            repository.updateProposalStatus(proposalId, newStatus)

            // Log action for proposal
            repository.insertAuditLog(
                AuditLog(
                    id = UUID.randomUUID().toString(),
                    action = "UPDATE_PROPOSAL_STATUS",
                    actorId = actorId,
                    archiveId = null,
                    proposalId = proposalId,
                    beritaAcaraId = null,
                    previousStatus = currentStatus,
                    newStatus = newStatus,
                    notes = "Mengubah status berkas usul musnah ${proposal.nomorBerkas} menjadi $newStatus",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Log for each archive in the proposal
            val archives = repository.getArchivesByProposal(proposalId).first()
            for (archive in archives) {
                repository.insertAuditLog(
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

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
