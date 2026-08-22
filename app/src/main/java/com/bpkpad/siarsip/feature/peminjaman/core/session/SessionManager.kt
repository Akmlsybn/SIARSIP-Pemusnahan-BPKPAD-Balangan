package com.bpkpad.peminjaman.core.session

import android.content.Context
import com.bpkpad.peminjaman.peminjaman.domain.model.enums.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val defaultSession = SessionObject(
        userId = 1,
        username = "arsiparis",
        namaLengkap = "Budi Santoso",
        role = UserRole.ARSIPARIS,
        noHp = "08123456789"
    )

    private val _sessionState = MutableStateFlow<SessionObject?>(defaultSession)
    val session: Flow<SessionObject?> = _sessionState.asStateFlow()
    val isLoggedIn: Flow<Boolean> = MutableStateFlow(true).asStateFlow()

    suspend fun saveSession(sessionObject: SessionObject) {
        _sessionState.value = sessionObject
    }

    suspend fun clearSession() {
        _sessionState.value = null
    }
}
