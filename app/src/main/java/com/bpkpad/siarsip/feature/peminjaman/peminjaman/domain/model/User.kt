package com.bpkpad.peminjaman.peminjaman.domain.model

import com.bpkpad.peminjaman.peminjaman.domain.model.enums.UserRole

data class User(
    val id: Int = 1,
    val username: String = "arsiparis",
    val nama: String = "Budi Santoso",
    val nip: String = "19850101 201001 1 001",
    val jab: String = "Arsiparis BPKPAD",
    val role: UserRole = UserRole.ARSIPARIS
)
