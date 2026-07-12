package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.LogEntry
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetAuditLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LogRiwayatViewModel @Inject constructor(
    private val getAuditLogsUseCase: GetAuditLogsUseCase
) : ViewModel() {

    val uiState: StateFlow<ResultState<List<LogEntry>>> = getAuditLogsUseCase()
        .map { logs ->
            val filteredLogs = logs.filter { it.categoryName != "Sistem" }
            ResultState.Success(filteredLogs) as ResultState<List<LogEntry>>
        }
        .onStart {
            emit(ResultState.Loading)
        }
        .catch { exception ->
            emit(ResultState.Error(exception))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultState.Loading
        )
}
