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

    val uiState: StateFlow<ResultState<Map<String, List<LogEntry>>>> = getAuditLogsUseCase()
        .map { logs ->
            // Filter out system logs
            val filteredLogs = logs.filter { it.categoryName != "Sistem" }
            
            // Group logs by proposal/document name, fallback to "Dokumen Lainnya" if null
            val grouped = filteredLogs.groupBy { it.relatedBerkas ?: "Dokumen Lainnya" }
            
            // Sort groups so that the group containing the newest log is at the top
            val sortedGrouped = grouped.toList()
                .sortedByDescending { pair -> pair.second.maxOfOrNull { it.sortKey } ?: 0L }
                .toMap()

            ResultState.Success(sortedGrouped) as ResultState<Map<String, List<LogEntry>>>
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
