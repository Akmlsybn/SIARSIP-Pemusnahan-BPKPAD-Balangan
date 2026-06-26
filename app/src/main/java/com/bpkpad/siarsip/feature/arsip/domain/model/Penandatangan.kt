package com.bpkpad.siarsip.feature.arsip.domain.model

data class Penandatangan(
    val id: String,
    val beritaAcaraId: String,
    val nama: String,
    val jabatan: String,
    val role: String,
    val urutan: Int
)
