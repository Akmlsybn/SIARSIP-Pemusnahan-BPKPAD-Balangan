package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetTrackingInfoUseCase
import com.bpkpad.siarsip.feature.arsip.domain.usecase.TrackingBerkas
import com.bpkpad.siarsip.feature.arsip.domain.usecase.UpdateProposalStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusTrackingViewModel @Inject constructor(
    private val getTrackingInfoUseCase: GetTrackingInfoUseCase,
    private val updateProposalStatusUseCase: UpdateProposalStatusUseCase
) : ViewModel() {

    val trackingList: StateFlow<ResultState<List<TrackingBerkas>>> = getTrackingInfoUseCase()
        .map { list ->
            ResultState.Success(list) as ResultState<List<TrackingBerkas>>
        }
        .onStart { emit(ResultState.Loading) }
        .catch { emit(ResultState.Error(it)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultState.Loading
        )

    private val _updateState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val updateState: StateFlow<ResultState<Unit>> = _updateState.asStateFlow()

    fun updateProposalStatus(proposalId: String, newStatus: String) {
        viewModelScope.launch {
            _updateState.value = ResultState.Loading
            updateProposalStatusUseCase(
                proposalId = proposalId,
                newStatus = newStatus,
                actorId = "admin" // Default actor
            ).fold(
                onSuccess = {
                    _updateState.value = ResultState.Success(Unit)
                },
                onFailure = {
                    _updateState.value = ResultState.Error(it)
                }
            )
        }
    }

    fun resetUpdateState() {
        _updateState.value = ResultState.Success(Unit)
    }
}
