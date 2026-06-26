package com.bpkpad.siarsip.feature.auth.domain.repository

import com.bpkpad.siarsip.feature.auth.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): User?
}