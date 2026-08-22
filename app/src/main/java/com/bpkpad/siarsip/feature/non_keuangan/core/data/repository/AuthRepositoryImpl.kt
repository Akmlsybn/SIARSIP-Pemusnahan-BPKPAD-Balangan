package com.bpkpad.arsip.core.data.repository

import android.content.SharedPreferences
import com.bpkpad.arsip.core.data.local.dao.UserDao
import com.bpkpad.arsip.core.domain.model.UserRole
import com.bpkpad.arsip.core.domain.model.UserSession
import com.bpkpad.arsip.core.domain.repository.AuthRepository
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @Named("nonKeuanganUserDao") private val userDao: UserDao,
    private val sharedPreferences: SharedPreferences
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<UserSession> {
        return try {
            val user = userDao.getUserByUsername(username)
                ?: throw Exception("User with username '$username' not found")
            
            if (user.password != password) {
                throw Exception("Invalid password")
            }
            
            val session = UserSession(
                userId = user.id,
                username = user.username,
                role = UserRole.valueOf(user.role),
                instance = user.instance,
                accessToken = "local_token_${user.id}",
                refreshToken = "local_refresh_token_${user.id}",
                expiresAt = System.currentTimeMillis() + 3600000 * 24
            )
            
            saveSession(session)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshSession(): Result<UserSession> {
        return try {
            val session = getActiveSession() ?: throw Exception("No active session")
            val updatedSession = session.copy(
                expiresAt = System.currentTimeMillis() + 3600000 * 24
            )
            saveSession(updatedSession)
            Result.success(updatedSession)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getActiveSession(): UserSession? {
        val sessionJson = sharedPreferences.getString("user_session", null)
        return if (sessionJson != null) {
            try {
                val json = JSONObject(sessionJson)
                UserSession(
                    userId = json.optString("userId"),
                    username = json.optString("username"),
                    role = UserRole.valueOf(json.optString("role", "ARSIPARIS")),
                    instance = json.optString("instance"),
                    accessToken = json.optString("accessToken"),
                    refreshToken = json.optString("refreshToken"),
                    expiresAt = json.optLong("expiresAt")
                )
            } catch (e: Exception) {
                mockSession()
            }
        } else {
            mockSession()
        }
    }

    private fun mockSession(): UserSession = UserSession(
        userId = "mock-id-123",
        username = "dev_arsiparis",
        role = UserRole.ARSIPARIS,
        instance = "BPKPAD",
        accessToken = "mock_access_token",
        refreshToken = "mock_refresh_token",
        expiresAt = System.currentTimeMillis() + 3600000 * 24
    )

    override fun isSessionValid(): Boolean {
        return true
    }

    private fun saveSession(session: UserSession) {
        val json = JSONObject().apply {
            put("userId", session.userId)
            put("username", session.username)
            put("role", session.role.name)
            put("instance", session.instance)
            put("accessToken", session.accessToken)
            put("refreshToken", session.refreshToken)
            put("expiresAt", session.expiresAt)
        }
        sharedPreferences.edit()
            .putString("user_session", json.toString())
            .apply()
    }

    private fun clearSession() {
        sharedPreferences.edit()
            .remove("user_session")
            .apply()
    }
}