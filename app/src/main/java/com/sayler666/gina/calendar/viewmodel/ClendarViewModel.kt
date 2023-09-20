package com.sayler666.gina.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.journal.usecase.GetDaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    getDaysUseCase: GetDaysUseCase,
    daysMapper: CalendarMapper
) : ViewModel() {

    val days = getDaysUseCase
        .getAllDaysFlow()
        .map {
            daysMapper.mapToVm(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            ginaDatabaseProvider.openSavedDB()
        }
    }
}
