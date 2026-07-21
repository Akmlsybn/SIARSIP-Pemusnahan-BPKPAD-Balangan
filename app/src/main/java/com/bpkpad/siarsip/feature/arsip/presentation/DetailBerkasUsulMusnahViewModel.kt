package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.BerkasUsulMusnah
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetProposalByNomorUseCase
import com.bpkpad.siarsip.feature.arsip.domain.usecase.RemoveArchiveFromProposalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailBerkasUsulMusnahViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProposalByNomorUseCase: GetProposalByNomorUseCase,
    private val removeArchiveFromProposalUseCase: RemoveArchiveFromProposalUseCase
) : ViewModel() {

    val berkasNomor: String = savedStateHandle["berkasNomor"] ?: ""

    val proposalState: StateFlow<ResultState<BerkasUsulMusnah?>> = getProposalByNomorUseCase(berkasNomor)
        .map { proposal ->
            ResultState.Success(proposal) as ResultState<BerkasUsulMusnah?>
        }
        .onStart { emit(ResultState.Loading) }
        .catch { emit(ResultState.Error(it)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultState.Loading
        )

    private val _removeState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val removeState: StateFlow<ResultState<Unit>> = _removeState.asStateFlow()

    fun removeArchive(proposalId: String, archiveId: String) {
        viewModelScope.launch {
            _removeState.value = ResultState.Loading
            removeArchiveFromProposalUseCase(proposalId, archiveId, "admin").fold(
                onSuccess = {
                    _removeState.value = ResultState.Success(Unit)
                },
                onFailure = {
                    _removeState.value = ResultState.Error(it)
                }
            )
        }
    }

    fun resetRemoveState() {
        _removeState.value = ResultState.Success(Unit)
    }
}
