package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetAllArchivesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DaftarArsipViewModel @Inject constructor(
    private val getAllArchivesUseCase: GetAllArchivesUseCase
) : ViewModel() {

    private val _beritaAcaraFilter = MutableStateFlow<String?>(null)
    val beritaAcaraFilter: StateFlow<String?> = _beritaAcaraFilter.asStateFlow()

    val uiState: StateFlow<ResultState<List<Arsip>>> = combine(
        getAllArchivesUseCase(),
        _beritaAcaraFilter
    ) { archives, filter ->
        if (filter != null) {
            archives.filter { it.beritaAcaraId == filter }
        } else {
            archives.filter { it.status != "DISPOSED" }
        }
    }
    .map { filteredArchives ->
        ResultState.Success(filteredArchives) as ResultState<List<Arsip>>
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

    fun setBeritaAcaraFilter(baId: String?) {
        _beritaAcaraFilter.value = baId
    }

    fun clearBeritaAcaraFilter() {
        _beritaAcaraFilter.value = null
    }
}
