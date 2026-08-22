package com.example.arsipbpkpad.domain.model

/**
 * Domain model for parsed metadata.
 */
data class ParsedMetadata(
    val docNumber: String? = null,
    val year: Int? = null,
    val subject: String? = null,
    val docType: String? = null,
    val nominal: Double? = null,
    val isArchiveDocument: Boolean = true
)
