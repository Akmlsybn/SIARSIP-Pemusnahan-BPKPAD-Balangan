package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import com.bpkpad.siarsip.feature.arsip.domain.usecase.CreateProposalUseCase
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetAvailableArchivesUseCase
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetProposalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BuatBerkasUsulMusnahViewModel @Inject constructor(
    private val getAvailableArchivesUseCase: GetAvailableArchivesUseCase,
    private val getProposalsUseCase: GetProposalsUseCase,
    private val createProposalUseCase: CreateProposalUseCase
) : ViewModel() {

    private val _saveState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val saveState: StateFlow<ResultState<Unit>> = _saveState.asStateFlow()

    val availableArchives: StateFlow<ResultState<List<Arsip>>> = getAvailableArchivesUseCase()
        .map { archives ->
            ResultState.Success(archives) as ResultState<List<Arsip>>
        }
        .onStart { emit(ResultState.Loading) }
        .catch { emit(ResultState.Error(it)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResultState.Loading
        )

    val nextProposalNumber: StateFlow<String> = getProposalsUseCase()
        .map { proposals ->
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val yearlyProposals = proposals.filter { it.nomorBerkas.startsWith("BUM-$currentYear") }
            val nextNum = if (yearlyProposals.isEmpty()) {
                1
            } else {
                val latestProposal = yearlyProposals.maxByOrNull { it.createdAt }
                val nomor = latestProposal?.nomorBerkas ?: ""
                val lastPart = nomor.substringAfterLast("-").toIntOrNull() ?: 0
                lastPart + 1
            }
            "BUM-$currentYear-${String.format("%03d", nextNum)}"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "BUM-${Calendar.getInstance().get(Calendar.YEAR)}-001"
        )

    fun createProposal(
        tanggal: String,
        unitPengolah: String,
        sumberModul: String,
        perihal: String,
        archiveIds: List<String>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _saveState.value = ResultState.Loading
            val nomor = nextProposalNumber.value
            createProposalUseCase(
                nomorBerkas = nomor,
                tanggal = tanggal,
                unitPengolah = unitPengolah,
                sumberModul = sumberModul,
                perihal = perihal,
                archiveIds = archiveIds,
                actorId = "admin" // Default actor
            ).fold(
                onSuccess = {
                    _saveState.value = ResultState.Success(Unit)
                    onSuccess()
                },
                onFailure = {
                    _saveState.value = ResultState.Error(it)
                }
            )
        }
    }

    fun resetSaveState() {
        _saveState.value = ResultState.Success(Unit)
    }
}
