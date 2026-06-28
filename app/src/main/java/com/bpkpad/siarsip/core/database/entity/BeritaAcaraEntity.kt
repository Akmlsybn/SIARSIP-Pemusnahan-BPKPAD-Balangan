package com.bpkpad.siarsip.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "berita_acara")
data class BeritaAcaraEntity(
    @PrimaryKey
    val id: String, // UUID v4
    val nomorBa: String,
    val tanggalEksekusi: String,
    val penanggungJawab: String,
    val saksi1: String,
    val saksi2: String?,
    val keterangan: String?,
    val metode: String = "Pencacahan",
    val createdAt: Long
)
