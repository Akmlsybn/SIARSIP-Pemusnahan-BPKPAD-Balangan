package com.bpkpad.siarsip.feature.auth.domain.repository

import com.bpkpad.siarsip.feature.auth.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): User?
    fun setRememberMe(remember: Boolean)
    fun isRememberMe(): Boolean
    fun setLoggedInUser(username: String?)
    fun getLoggedInUser(): String?
    fun logout()
}