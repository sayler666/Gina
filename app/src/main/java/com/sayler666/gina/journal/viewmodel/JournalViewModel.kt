package com.sayler666.gina.journal.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.AttachmentWithDay
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.journal.usecase.GetDaysUseCase
import com.sayler666.gina.journal.usecase.PreviousYearsAttachmentsUseCase
import com.sayler666.gina.journal.viewmodel.JournalState.LoadingState
import com.sayler666.gina.journal.viewmodel.JournalState.PermissionNeededState
import com.sayler666.gina.mood.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val getDaysUseCase: GetDaysUseCase,
    private val daysMapper: DaysMapper,
    private val previousYearsAttachmentsUseCase: PreviousYearsAttachmentsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _moodFilters = MutableStateFlow<List<Mood>>(Mood.entries)
    val moodFilters: StateFlow<List<Mood>> = _moodFilters

    val filtersActive: StateFlow<Boolean> = _moodFilters.map { moods ->
        moods.size != Mood.entries.size
    }.stateIn(
        viewModelScope,
        WhileSubscribed(500),
        false
    )

    private val _state = MutableStateFlow(
        if (Environment.isExternalStorageManager()) LoadingState else PermissionNeededState
    )
    val state: StateFlow<JournalState> = _state.asStateFlow()

    private val _attachments = MutableStateFlow<List<AttachmentWithDay>>(emptyList())

    init {
        initDb()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun initDb() {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
        viewModelScope.launch {
            previousYearsAttachmentsUseCase().collect { attachments ->
                _attachments.value = attachments
            }
        }

        viewModelScope.launch {
            combine(_moodFilters, _searchQuery, _attachments) { moods, search, attachments ->
                Triple(search, moods, attachments)
            }.flatMapLatest { triple ->
                getDaysUseCase
                    .getFilteredDaysFlow(triple.first, triple.second)
                    .map {
                        daysMapper.toJournalState(
                            it,
                            triple.first,
                            triple.second,
                            triple.third
                        )
                    }
            }.collect(_state::tryEmit)
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
        _moodFilters.update { Mood.entries }
    }
}
