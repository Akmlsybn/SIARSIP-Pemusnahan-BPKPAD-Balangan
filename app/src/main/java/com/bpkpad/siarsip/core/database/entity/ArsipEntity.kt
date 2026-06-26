package com.bpkpad.siarsip.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "archives")
data class ArsipEntity(
    @PrimaryKey
    val id: String, // UUID v4
    val kode: String,
    val fullKode: String,
    val deskripsi: String,
    val tahun: String,
    val tingkat: String,
    val volume: String,
    val retensiAktif: String,
    val retensiInaktif: String,
    val keterangan: String, // "Musnah" atau "Permanen"
    val sumber: String,     // "Keuangan", "Non-Keuangan", "Peminjaman"
    val status: String,     // AVAILABLE, PROPOSED, VERIFIED, APPROVED, DISPOSED
    val proposalId: String? = null,
    val beritaAcaraId: String? = null,
    val disposedAt: String? = null
)
