package com.bpkpad.siarsip.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "penandatangan",
    foreignKeys = [
        ForeignKey(
            entity = BeritaAcaraEntity::class,
            parentColumns = ["id"],
            childColumns = ["beritaAcaraId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["beritaAcaraId"])]
)
data class PenandatanganEntity(
    @PrimaryKey
    val id: String, // UUID v4
    val beritaAcaraId: String,
    val nama: String,
    val jabatan: String,
    val role: String, // e.g. SAKSI_1, SAKSI_2, PENANGGUNG_JAWAB
    val urutan: Int
)
