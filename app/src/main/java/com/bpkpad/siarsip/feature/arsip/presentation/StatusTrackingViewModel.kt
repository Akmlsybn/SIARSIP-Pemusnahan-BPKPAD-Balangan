package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetTrackingInfoUseCase
import com.bpkpad.siarsip.feature.arsip.domain.usecase.TrackingBerkas
import com.bpkpad.siarsip.feature.arsip.domain.usecase.UpdateProposalStatusUseCase
import com.bpkpad.siarsip.feature.arsip.domain.usecase.DeleteProposalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusTrackingViewModel @Inject constructor(
    private val getTrackingInfoUseCase: GetTrackingInfoUseCase,
    private val updateProposalStatusUseCase: UpdateProposalStatusUseCase,
    private val deleteProposalUseCase: DeleteProposalUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val trackingList: StateFlow<ResultState<List<TrackingBerkas>>> = combine(
        getTrackingInfoUseCase(),
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { berkas ->
                berkas.nomor.contains(query, ignoreCase = true) ||
                berkas.perihal.contains(query, ignoreCase = true) ||
                berkas.sumber.contains(query, ignoreCase = true)
            }
        }
    }
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

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _updateState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val updateState: StateFlow<ResultState<Unit>> = _updateState.asStateFlow()

    fun updateProposalStatus(
        proposalId: String,
        newStatus: String,
        suratPertimbanganNomor: String? = null,
        suratPertimbanganPerihal: String? = null,
        jenisPersetujuanAkhir: String? = null,
        nomorPersetujuanAkhir: String? = null,
        perihalPersetujuanAkhir: String? = null
    ) {
        viewModelScope.launch {
            _updateState.value = ResultState.Loading
            updateProposalStatusUseCase(
                proposalId = proposalId,
                newStatus = newStatus,
                actorId = "admin", // Default actor
                suratPertimbanganNomor = suratPertimbanganNomor,
                suratPertimbanganPerihal = suratPertimbanganPerihal,
                jenisPersetujuanAkhir = jenisPersetujuanAkhir,
                nomorPersetujuanAkhir = nomorPersetujuanAkhir,
                perihalPersetujuanAkhir = perihalPersetujuanAkhir
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

    fun deleteProposalForRevision(proposalId: String) {
        viewModelScope.launch {
            _updateState.value = ResultState.Loading
            deleteProposalUseCase(proposalId, "admin").fold(
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
