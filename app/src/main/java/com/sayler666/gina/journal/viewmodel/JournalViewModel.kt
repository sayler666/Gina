package com.sayler666.gina.journal.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.journal.usecase.GetDaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class JournalViewModel @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val getDaysUseCase: GetDaysUseCase,
    private val daysMapper: DaysMapper
) : ViewModel() {

    private val _permissionGranted = MutableStateFlow(Environment.isExternalStorageManager())
    val permissionGranted: StateFlow<Boolean> = _permissionGranted

    private val _searchQuery = MutableStateFlow<String?>(null)

    private val _daysSearch = MutableStateFlow(JournalSearchState())
    val daysSearch: StateFlow<JournalSearchState> = _daysSearch

    private val _days = MutableStateFlow<List<DayEntity>>(emptyList())
    val days: StateFlow<List<DayEntity>> = _days

    init {
        initDb()
    }

    private fun initDb() {
        viewModelScope.launch { databaseProvider.openSavedDB() }
        viewModelScope.launch {
            _searchQuery.flatMapLatest { query ->
                getDaysUseCase
                    .getDaysFlow(query)
                    .map { daysMapper.mapToVm(it, query) }
                    .map { JournalSearchState(it, _searchQuery.value) }
            }.collect(_daysSearch::tryEmit)
        }
        viewModelScope.launch {
            getDaysUseCase
                .getDaysFlow()
                .map(daysMapper::mapToVm)
                .collect(_days::tryEmit)
        }
    }

    fun refreshPermissionStatus() {
        _permissionGranted.value = Environment.isExternalStorageManager()
        if (_permissionGranted.value) initDb()
    }

    fun searchQuery(searchQuery: String?) {
        _searchQuery.update { searchQuery }
    }
}
