package com.example.arsipbpkpad.data.remote.dto





data class DocumentTypeDto(
     val code: String,
     val name: String,
     val isSystem: Boolean = false,
     val isActive: Boolean = true,
     val createdBy: String? = null
)
