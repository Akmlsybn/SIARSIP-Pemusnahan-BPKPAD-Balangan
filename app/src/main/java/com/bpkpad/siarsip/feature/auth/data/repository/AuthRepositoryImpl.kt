package com.bpkpad.siarsip.feature.auth.data.repository

import com.bpkpad.siarsip.core.database.dao.UserDao
import com.bpkpad.siarsip.feature.auth.data.mapper.toDomain
import com.bpkpad.siarsip.feature.auth.domain.model.User
import com.bpkpad.siarsip.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(username: String, password: String): User? {
        val entity = userDao.findByUsername(username) ?: return null
        if (entity.passwordHash != sha256(password)) return null
        return entity.toDomain()
    }

    private fun sha256(text: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}