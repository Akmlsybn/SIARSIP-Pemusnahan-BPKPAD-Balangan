package com.bpkpad.siarsip.feature.arsip.domain.model

data class Arsip(
    val id: String,
    val kode: String,
    val fullKode: String,
    val deskripsi: String,
    val tahun: String,
    val tingkat: String,
    val volume: String,
    val retensiAktif: String,
    val retensiInaktif: String,
    val keterangan: String,
    val sumber: String,
    val status: String,
    val proposalId: String?,
    val beritaAcaraId: String?,
    val disposedAt: String?
)
