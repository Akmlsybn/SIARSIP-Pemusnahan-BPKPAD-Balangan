package com.bpkpad.siarsip.feature.arsip.domain.model

data class BeritaAcara(
    val id: String,
    val nomorBa: String,
    val tanggalEksekusi: String,
    val penanggungJawab: String,
    val saksi1: String,
    val saksi2: String?,
    val keterangan: String?,
    val metode: String = "Pencacahan",
    val createdAt: Long,
    val signatories: List<Penandatangan> = emptyList(),
    val archives: List<Arsip> = emptyList()
)
