package com.sayler666.gina.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.daysList.usecase.GetDaysUseCase
import com.sayler666.gina.db.DatabaseProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    getDaysUseCase: GetDaysUseCase,
    daysMapper: CalendarMapper
) : ViewModel() {

    val days = getDaysUseCase
        .getDaysFlow()
        .map {
            daysMapper.mapToVm(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            databaseProvider.openSavedDB()
        }
    }
}
