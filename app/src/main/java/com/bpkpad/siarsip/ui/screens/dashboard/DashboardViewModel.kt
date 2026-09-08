package com.bpkpad.siarsip.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.bpkpad.siarsip.feature.auth.domain.repository.AuthRepository

data class PemusnahanDashboardUiState(
    val totalArsip: Int = 183000,
    val usulMusnah: Int = 0,
    val tersedia: Int = 0,
    val musnah: Int = 0,
    val isLoading: Boolean = false,
    val userName: String = "Admin Arsip",
    val userRole: String = "Tim Pemusnahan Arsip",
    val isKasubbag: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: ArsipRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PemusnahanDashboardUiState())
    val uiState: StateFlow<PemusnahanDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardStats()
    }

    fun loadDashboardStats() {
        val loggedInUser = authRepository.getLoggedInUser()
        if (loggedInUser == "kasubbag") {
            _uiState.update {
                it.copy(
                    userName = "Kasubbag Umum & Kearsipan",
                    userRole = "Penilai & Pemantau Pemusnahan",
                    isKasubbag = true
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    userName = "Admin Arsip",
                    userRole = "Tim Pemusnahan Arsip",
                    isKasubbag = false
                )
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.countArchivesFiltered(null, null, null, null).collect { total ->
                _uiState.update { it.copy(totalArsip = if (total > 0) total else 183000) }
            }
        }
        viewModelScope.launch {
            repository.countArchivesFiltered(null, "PROPOSED", null, null).collect { proposed ->
                _uiState.update { it.copy(usulMusnah = proposed) }
            }
        }
        viewModelScope.launch {
            repository.countArchivesFiltered(null, "AVAILABLE", null, null).collect { available ->
                _uiState.update { it.copy(tersedia = available) }
            }
        }
        viewModelScope.launch {
            repository.countArchivesFiltered(null, "DISPOSED", null, null).collect { disposed ->
                _uiState.update { it.copy(musnah = disposed, isLoading = false) }
            }
        }
    }
}
