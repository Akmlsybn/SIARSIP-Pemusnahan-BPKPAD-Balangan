package com.bpkpad.peminjaman.peminjaman.data.repository

import com.bpkpad.peminjaman.core.common.ResultState
import com.bpkpad.peminjaman.core.database.dao.UserDao
import com.bpkpad.peminjaman.peminjaman.domain.model.User
import com.bpkpad.peminjaman.peminjaman.domain.model.enums.UserRole
import com.bpkpad.peminjaman.peminjaman.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    private val defaultUser = User(
        id = 1,
        username = "arsiparis",
        nama = "Budi Santoso",
        nip = "19850101 201001 1 001",
        jab = "Arsiparis BPKPAD",
        role = UserRole.ARSIPARIS
    )

    override suspend fun login(username: String, password: String): ResultState<User> {
        return ResultState.Success(defaultUser)
    }

    override suspend fun getAuthenticatedUser(): User? {
        return defaultUser
    }

    override suspend fun logout() {
    }

    override suspend fun getUserById(id: Int): User? {
        return defaultUser
    }

    override suspend fun getAllUsers(): List<User> {
        return listOf(defaultUser)
    }
}
