package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.BeritaAcaraItem
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetBeritaAcaraByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DetailBeritaAcaraViewModel @Inject constructor(
    private val getBeritaAcaraByIdUseCase: GetBeritaAcaraByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val baId: String = savedStateHandle.get<String>("baId") ?: ""

    val uiState: StateFlow<ResultState<BeritaAcaraItem>> = if (baId.isBlank()) {
        flowOf(ResultState.Error(Exception("ID Berita Acara tidak valid")))
    } else {
        getBeritaAcaraByIdUseCase(baId)
            .map { ba ->
                if (ba != null) {
                    ResultState.Success(ba) as ResultState<BeritaAcaraItem>
                } else {
                    ResultState.Error(Exception("Berita Acara tidak ditemukan"))
                }
            }
            .catch { exception ->
                emit(ResultState.Error(exception))
            }
    }.onStart {
        emit(ResultState.Loading)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResultState.Loading
    )
}
