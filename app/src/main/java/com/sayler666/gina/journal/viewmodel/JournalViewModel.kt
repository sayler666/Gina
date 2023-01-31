package com.sayler666.gina.journal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.journal.usecase.GetDaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val getDaysUseCase: GetDaysUseCase,
    private val daysMapper: DaysMapper
) : ViewModel() {

    private val _searchQuery = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val daysSearch: StateFlow<JournalSearchState> = _searchQuery.flatMapLatest { query ->
        getDaysUseCase
            .getDaysFlow(query)
            .map(daysMapper::mapToVm)
            .map { JournalSearchState(it, _searchQuery.value) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = JournalSearchState()
    )

    val days: StateFlow<List<DayEntity>>
        get() = getDaysUseCase
            .getDaysFlow()
            .map(daysMapper::mapToVm)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun searchQuery(searchQuery: String?) {
        _searchQuery.update { searchQuery }
    }

    init {
        viewModelScope.launch {
            databaseProvider.openSavedDB()
        }
    }
}
