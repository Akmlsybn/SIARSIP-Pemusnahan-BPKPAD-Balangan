package com.bpkpad.peminjaman.peminjaman.data.remote

data class ArchiveDocumentDto(
    val id: String,
    val documentType: String,
    val documentNumber: String? = null,
    val title: String,
    val description: String? = null,
    val year: Int,
    val status: String,
    val storageLocation: StorageLocationDto? = null
)

data class StorageLocationDto(
    val room: String,
    val shelf: String,
    val boxNumber: String? = null
)
