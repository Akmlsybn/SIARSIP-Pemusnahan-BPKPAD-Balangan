package com.bpkpad.siarsip.feature.arsip.domain.model

data class BeritaAcaraItem(
    val id: String,
    val nomor: String,
    val berkasNomor: String,
    val perihal: String,
    val tanggal: String,
    val tanggalShort: String,
    val lokasi: String,
    val metode: String,
    val jumlahArsip: Int,
    val sumber: String,
    val tahun: String,
    val penandatangan: List<Penandatangan>
)
