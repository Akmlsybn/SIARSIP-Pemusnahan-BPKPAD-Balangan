package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import com.bpkpad.siarsip.feature.arsip.domain.usecase.CreateProposalUseCase
import com.bpkpad.siarsip.feature.arsip.domain.usecase.GetProposalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BuatBerkasUsulMusnahViewModel @Inject constructor(
    private val repository: ArsipRepository,
    private val getProposalsUseCase: GetProposalsUseCase,
    private val createProposalUseCase: CreateProposalUseCase
) : ViewModel() {

    private val _saveState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val saveState: StateFlow<ResultState<Unit>> = _saveState.asStateFlow()

    private val _sumberFilter = MutableStateFlow("Keuangan")
    val sumberFilter: StateFlow<String> = _sumberFilter.asStateFlow()

    private val _tahunFilter = MutableStateFlow("Semua")
    val tahunFilter: StateFlow<String> = _tahunFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _pageSize = MutableStateFlow(25)
    val pageSize: StateFlow<Int> = _pageSize.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    init {
        // Automatic reset to page 0 whenever any filter or search query changes!
        combine(_sumberFilter, _tahunFilter, _searchQuery, _pageSize) { _, _, _, _ ->
            Unit
        }.onEach {
            _currentPage.value = 0
        }.launchIn(viewModelScope)
    }

    val totalCount: StateFlow<Int> = combine(
        _sumberFilter,
        _tahunFilter,
        _searchQuery
    ) { sumber, tahun, query ->
        repository.countArchivesFiltered(
            sumber = sumber,
            status = "AVAILABLE",
            tahun = tahun,
            query = query
        )
    }.flatMapLatest { flow -> flow }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val availableArchives: StateFlow<ResultState<List<Arsip>>> = combine(
        listOf(_sumberFilter, _tahunFilter, _searchQuery, _pageSize, _currentPage)
    ) { array ->
        val sumber = array[0] as String
        val tahun = array[1] as String
        val query = array[2] as String
        val pageSize = array[3] as Int
        val page = array[4] as Int
        val offset = page * pageSize

        repository.getArchivesFiltered(
            sumber = sumber,
            status = "AVAILABLE",
            tahun = tahun,
            query = query,
            limit = pageSize,
            offset = offset
        )
    }.flatMapLatest { flow ->
        flow.map { list -> ResultState.Success(list) as ResultState<List<Arsip>> }
    }.onStart {
        emit(ResultState.Loading)
    }.catch {
        emit(ResultState.Error(it))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResultState.Loading)

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

    fun setSumberFilter(sumber: String) {
        _sumberFilter.value = sumber
    }

    fun setTahunFilter(tahun: String) {
        _tahunFilter.value = tahun
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPageSize(size: Int) {
        if (size in listOf(25, 50, 100)) {
            _pageSize.value = size
        }
    }

    fun nextPage(maxPage: Int) {
        if (_currentPage.value < maxPage - 1) {
            _currentPage.value += 1
        }
    }

    fun previousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value -= 1
        }
    }

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
