package com.sayler666.gina.daysList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.daysList.usecase.GetDaysUseCase
import com.sayler666.gina.db.DatabaseProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DaysListViewModel @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    getDaysUseCase: GetDaysUseCase,
    daysMapper: DaysMapper
) : ViewModel() {

    private val _searchQuery = MutableStateFlow<String?>(null)
    val daysSearch = _searchQuery.flatMapLatest { query ->
        getDaysUseCase
            .getDaysFlow(query)
            .map {
                daysMapper.mapToVm(it)
            }
    }.stateIn(
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
