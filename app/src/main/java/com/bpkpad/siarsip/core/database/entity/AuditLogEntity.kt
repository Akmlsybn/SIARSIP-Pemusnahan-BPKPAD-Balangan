package com.bpkpad.siarsip.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey
    val id: String, // UUID v4
    val action: String, // e.g., CREATE_PROPOSAL, VERIFY, APPROVE, DISPOSE, etc.
    val actorId: String,
    val archiveId: String? = null,
    val proposalId: String? = null,
    val beritaAcaraId: String? = null,
    val previousStatus: String? = null,
    val newStatus: String? = null,
    val notes: String? = null,
    val timestamp: Long
)
