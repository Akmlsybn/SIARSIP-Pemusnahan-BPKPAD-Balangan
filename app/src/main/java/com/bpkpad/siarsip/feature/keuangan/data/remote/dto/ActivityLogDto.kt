package com.example.arsipbpkpad.data.remote.dto

data class ActivityLogDto(
    val id: String? = null,
    val actorId: String? = null,
    val action: String,
    val entityType: String? = null,
    val entityId: String? = null,
    val metadata: String? = null,
    val createdAt: String? = null
)
