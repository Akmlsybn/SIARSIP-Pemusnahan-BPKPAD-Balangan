package com.example.arsipbpkpad.data.remote.dto

 



data class ArchiveDto(
     val id: String? = null,
     val type: String,
     val documentNumber: String? = null,
     val copyType: String,
     val copyCount: Int,
     val classificationCode: String = "900.1.3.1",
     val description: String? = null,
     val nominal: Double? = null,
     val year: Int,
     val condition: String,
     val status: String,
     val metadata: ArchiveMetadataDto? = null,
     val idStorageLocation: String? = null,
     val bundleId: String? = null,
     val createdBy: String? = null,
     val createdAt: String? = null,
     val updatedAt: String? = null,
     val deletedAt: String? = null
)


data class StorageLocationDto(
     val id: String? = null,
     val room: String,
     val shelf: String,
     val boxNumber: String,
     val description: String? = null,
     val createdBy: String? = null
)
