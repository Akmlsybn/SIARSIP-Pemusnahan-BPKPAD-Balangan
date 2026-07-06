package com.bpkpad.siarsip.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.feature.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileState(
    val username: String = "",
    val namaPegawai: String = "",
    val nip: String = "",
    val jabatan: String = ""
)

@HiltViewModel
class ProfilViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfileState?>(null)
    val userProfile: StateFlow<UserProfileState?> = _userProfile.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            val username = repository.getLoggedInUser() ?: "admin"
            val profile = if (username == "admin") {
                UserProfileState(
                    username = "admin",
                    namaPegawai = "Administrator SIARSIP",
                    nip = "198904122015031002",
                    jabatan = "Arsiparis Ahli Pertama - BPKPAD Balangan"
                )
            } else {
                UserProfileState(
                    username = username,
                    namaPegawai = username.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    nip = "199408252021021001",
                    jabatan = "Staf Pengelola Kearsipan - BPKPAD Balangan"
                )
            }
            _userProfile.value = profile
        }
    }

    fun changePassword(oldPass: String, newPass: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val username = repository.getLoggedInUser() ?: "admin"
            val result = repository.changePassword(username, oldPass, newPass)
            onResult(result)
        }
    }

    fun logout() {
        repository.logout()
    }
}
