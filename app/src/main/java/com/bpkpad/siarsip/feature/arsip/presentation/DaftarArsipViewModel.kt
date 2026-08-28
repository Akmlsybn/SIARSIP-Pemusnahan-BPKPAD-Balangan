package com.bpkpad.siarsip.feature.arsip.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DaftarArsipViewModel @Inject constructor(
    private val repository: ArsipRepository
) : ViewModel() {

    private val _sumberFilter = MutableStateFlow("Semua")
    val sumberFilter: StateFlow<String> = _sumberFilter.asStateFlow()

    private val _statusFilter = MutableStateFlow("Semua")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

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
        combine(_sumberFilter, _statusFilter, _tahunFilter, _searchQuery, _pageSize) { _, _, _, _, _ ->
            Unit
        }.onEach {
            _currentPage.value = 0
        }.launchIn(viewModelScope)
    }

    val totalCount: StateFlow<Int> = combine(
        _sumberFilter,
        _statusFilter,
        _tahunFilter,
        _searchQuery
    ) { sumber, status, tahun, query ->
        repository.countArchivesFiltered(sumber, status, tahun, query)
    }.flatMapLatest { flow -> flow }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val archivesUiState: StateFlow<ResultState<List<Arsip>>> = combine(
        listOf(_sumberFilter, _statusFilter, _tahunFilter, _searchQuery, _pageSize, _currentPage)
    ) { array ->
        val sumber = array[0] as String
        val status = array[1] as String
        val tahun = array[2] as String
        val query = array[3] as String
        val pageSize = array[4] as Int
        val page = array[5] as Int
        val offset = page * pageSize

        repository.getArchivesFiltered(
            sumber = sumber,
            status = status,
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

    fun setBeritaAcaraFilter(baId: String?) {
        if (baId != null) {
            _statusFilter.value = "DISPOSED"
        }
    }
}
