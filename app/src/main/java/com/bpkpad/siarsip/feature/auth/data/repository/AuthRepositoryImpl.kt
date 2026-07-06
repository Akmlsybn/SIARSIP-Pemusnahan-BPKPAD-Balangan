package com.bpkpad.siarsip.feature.auth.data.repository

import android.content.SharedPreferences
import com.bpkpad.siarsip.core.database.dao.UserDao
import com.bpkpad.siarsip.feature.auth.data.mapper.toDomain
import com.bpkpad.siarsip.feature.auth.domain.model.User
import com.bpkpad.siarsip.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val sharedPreferences: SharedPreferences
) : AuthRepository {

    override suspend fun login(username: String, password: String): User? {
        val entity = userDao.findByUsername(username) ?: return null
        if (entity.passwordHash != sha256(password)) return null
        return entity.toDomain()
    }

    override fun setRememberMe(remember: Boolean) {
        sharedPreferences.edit().putBoolean("remember_me", remember).apply()
    }

    override fun isRememberMe(): Boolean {
        return sharedPreferences.getBoolean("remember_me", false)
    }

    override fun setLoggedInUser(username: String?) {
        sharedPreferences.edit().putString("logged_in_user", username).apply()
    }

    override fun getLoggedInUser(): String? {
        return sharedPreferences.getString("logged_in_user", null)
    }

    override fun logout() {
        sharedPreferences.edit()
            .remove("logged_in_user")
            .remove("remember_me")
            .apply()
    }

    override suspend fun changePassword(username: String, oldPassword: String, newPassword: String): Result<Unit> {
        val entity = userDao.findByUsername(username) ?: return Result.failure(Exception("Pengguna tidak ditemukan"))
        if (entity.passwordHash != sha256(oldPassword)) {
            return Result.failure(Exception("Kata sandi lama salah"))
        }
        val rowsAffected = userDao.updatePassword(username, sha256(newPassword))
        return if (rowsAffected > 0) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Gagal mengubah kata sandi"))
        }
    }

    private fun sha256(text: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}