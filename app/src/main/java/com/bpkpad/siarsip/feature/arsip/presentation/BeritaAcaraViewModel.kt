package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.BeritaAcaraItem
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetBeritaAcaraUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BeritaAcaraViewModel @Inject constructor(
    private val getBeritaAcaraUseCase: GetBeritaAcaraUseCase
) : ViewModel() {

    val uiState: StateFlow<ResultState<List<BeritaAcaraItem>>> = getBeritaAcaraUseCase()
        .map { items ->
            ResultState.Success(items) as ResultState<List<BeritaAcaraItem>>
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
