package com.bpkpad.siarsip.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.feature.auth.domain.model.User
import com.bpkpad.siarsip.feature.auth.domain.usecase.LoginUseCase
import com.bpkpad.siarsip.feature.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = loginUseCase(username, password)
            result.onSuccess { user ->
                repository.setRememberMe(rememberMe)
                repository.setLoggedInUser(user.username)
                _uiState.value = LoginUiState.Success(user)
            }.onFailure { exception ->
                _uiState.value = LoginUiState.Error(exception.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
