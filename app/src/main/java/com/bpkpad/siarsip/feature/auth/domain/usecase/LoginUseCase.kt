package com.bpkpad.siarsip.feature.auth.domain.usecase

import com.bpkpad.siarsip.feature.auth.domain.model.User
import com.bpkpad.siarsip.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        if (username.isBlank()) return Result.failure(Exception("Username tidak boleh kosong"))
        if (password.isBlank()) return Result.failure(Exception("Password tidak boleh kosong"))

        val user = repository.login(username, password)
            ?: return Result.failure(Exception("Username atau password salah"))

        return Result.success(user)
    }
}