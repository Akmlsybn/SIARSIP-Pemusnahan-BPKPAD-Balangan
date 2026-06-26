package com.bpkpad.siarsip.feature.arsip.domain.model

data class AuditLog(
    val id: String,
    val action: String,
    val actorId: String,
    val archiveId: String?,
    val proposalId: String?,
    val beritaAcaraId: String?,
    val previousStatus: String?,
    val newStatus: String?,
    val notes: String?,
    val timestamp: Long
)
