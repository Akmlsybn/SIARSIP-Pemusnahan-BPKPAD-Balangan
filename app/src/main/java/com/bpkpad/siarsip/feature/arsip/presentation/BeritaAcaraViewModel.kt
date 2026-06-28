package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.BeritaAcaraItem
import com.bpkpad.siarsip.feature.arsip.domain.model.BerkasUsulMusnah
import com.bpkpad.siarsip.feature.arsip.domain.model.Penandatangan
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import com.bpkpad.siarsip.feature.arsip.domain.usecase.CreateBeritaAcaraUseCase
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetBeritaAcaraUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BeritaAcaraViewModel @Inject constructor(
    private val getBeritaAcaraUseCase: GetBeritaAcaraUseCase,
    private val createBeritaAcaraUseCase: CreateBeritaAcaraUseCase,
    private val repository: ArsipRepository
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

    val approvedProposals: StateFlow<List<BerkasUsulMusnah>> = repository.getAllProposals()
        .map { list ->
            list.filter { it.status == "APPROVED" }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _createState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val createState: StateFlow<ResultState<Unit>> = _createState.asStateFlow()

    fun createBeritaAcara(
        nomorBa: String,
        tanggalEksekusi: String,
        keterangan: String?,
        metode: String,
        proposalId: String,
        signatories: List<Penandatangan>
    ) {
        viewModelScope.launch {
            _createState.value = ResultState.Loading
            createBeritaAcaraUseCase(
                nomorBa = nomorBa,
                tanggalEksekusi = tanggalEksekusi,
                keterangan = keterangan,
                metode = metode,
                proposalId = proposalId,
                signatoriesInput = signatories,
                actorId = "admin"
            ).fold(
                onSuccess = {
                    _createState.value = ResultState.Success(Unit)
                },
                onFailure = {
                    _createState.value = ResultState.Error(it)
                }
            )
        }
    }

    fun resetCreateState() {
        _createState.value = ResultState.Success(Unit)
    }
}
