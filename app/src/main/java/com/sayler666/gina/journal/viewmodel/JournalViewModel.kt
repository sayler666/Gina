package com.sayler666.gina.journal.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.journal.usecase.GetDaysUseCase
import com.sayler666.gina.journal.viewmodel.JournalState.EmptyState
import com.sayler666.gina.journal.viewmodel.JournalState.PermissionNeededState
import com.sayler666.gina.ui.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val _searchQuery = MutableStateFlow("")
    private val _moodFilters = MutableStateFlow(Mood.values().asList())
    val moodFilters: StateFlow<List<Mood>> = _moodFilters

    val filtersActive: StateFlow<Boolean> = _moodFilters.map { moods ->
        moods.size != Mood.values().size
    }.stateIn(
        viewModelScope,
        WhileSubscribed(500),
        false
    )

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
            combine(_moodFilters, _searchQuery) { moods, search ->
                moods to search
            }.flatMapLatest { (moods, search) ->
                getDaysUseCase
                    .getFilteredDaysFlow(search, moods)
                    .map { daysMapper.toJournalState(it, search, moods) }
            }.collect {
                _state.tryEmit(it)
            }
        }
    }

    fun refreshPermissionStatus() {
        if (Environment.isExternalStorageManager()) initDb()
    }

    fun searchQuery(searchQuery: String) {
        _searchQuery.update { searchQuery }
    }

    fun updateMoodFilters(moods: List<Mood>) {
        _moodFilters.update { moods }
    }

    fun resetFilters() {
        _moodFilters.update { Mood.values().asList() }
    }
}
