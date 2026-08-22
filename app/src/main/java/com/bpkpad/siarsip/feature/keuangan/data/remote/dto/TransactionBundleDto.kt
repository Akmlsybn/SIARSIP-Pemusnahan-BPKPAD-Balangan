package com.example.arsipbpkpad.data.remote.dto





data class TransactionBundleDto(
     val id: String? = null,
     val bundleName: String,
     val description: String? = null,
     val year: Int,
     val createdBy: String? = null,
     val deletedAt: String? = null
)
