package com.sayler666.gina.insights.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.journal.usecase.GetDaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    getDaysUseCase: GetDaysUseCase,
    insightsMapper: InsightsMapper
) : ViewModel() {

    private val _searchQuery = MutableStateFlow<String?>(null)
    val insightsState = _searchQuery.flatMapLatest { query ->
        getDaysUseCase
            .getDaysFlow(query)
            .map(insightsMapper::toInsightsState)
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = null
    )

    fun searchQuery(searchQuery: String?) {
        _searchQuery.update { searchQuery }
    }

}
