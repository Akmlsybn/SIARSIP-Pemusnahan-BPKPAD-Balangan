package com.bpkpad.siarsip.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "proposals")
data class BerkasUsulMusnahEntity(
    @PrimaryKey
    val id: String, // UUID v4
    val nomorBerkas: String,
    val tanggal: String,
    val unitPengolah: String,
    val sumberModul: String,
    val perihal: String,
    val status: String, // PROPOSED, VERIFIED, APPROVED, DISPOSED
    val createdAt: Long
)
