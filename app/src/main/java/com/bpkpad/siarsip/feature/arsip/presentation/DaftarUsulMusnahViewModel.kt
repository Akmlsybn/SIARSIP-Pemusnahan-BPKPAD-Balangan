package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.BerkasUsulMusnah
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetProposalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DaftarUsulMusnahViewModel @Inject constructor(
    private val getProposalsUseCase: GetProposalsUseCase
) : ViewModel() {

    val uiState: StateFlow<ResultState<List<BerkasUsulMusnah>>> = getProposalsUseCase()
        .map { proposals ->
            ResultState.Success(proposals) as ResultState<List<BerkasUsulMusnah>>
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
