package com.example.arsipbpkpad.domain.model

data class DocumentType(
    val code: String,
    val name: String,
    val isSystem: Boolean = false,
    val isActive: Boolean = true
)
