package com.sayler666.gina.journal.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.journal.usecase.GetDaysUseCase
import com.sayler666.gina.journal.viewmodel.JournalState.EmptyState
import com.sayler666.gina.journal.viewmodel.JournalState.PermissionNeededState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _searchQuery = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow(
        if (Environment.isExternalStorageManager()) EmptyState else PermissionNeededState
    )
    val state = _state

    init {
        initDb()
    }

    private fun initDb() {
        viewModelScope.launch { databaseProvider.openSavedDB() }
        viewModelScope.launch {
            _searchQuery.flatMapLatest { searchQuery ->
                getDaysUseCase
                    .getDaysFlow(searchQuery)
                    .map { daysMapper.toJournalState(it, searchQuery) }
            }.collect {
                _state.tryEmit(it)
            }
        }
    }

    fun refreshPermissionStatus() {
        if (Environment.isExternalStorageManager()) initDb()
    }

    fun searchQuery(searchQuery: String?) {
        _searchQuery.update { searchQuery }
    }
}
