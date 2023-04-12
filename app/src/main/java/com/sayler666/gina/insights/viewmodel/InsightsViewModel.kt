package com.sayler666.gina.insights.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.insights.viewmodel.InsightState.EmptyState
import com.sayler666.gina.insights.viewmodel.InsightState.PermissionNeededState
import com.sayler666.gina.journal.usecase.GetDaysUseCase
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
class InsightsViewModel @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val getDaysUseCase: GetDaysUseCase,
    private val insightsMapper: InsightsMapper
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
                    .map { insightsMapper.toInsightsState(it, searchQuery) }
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
