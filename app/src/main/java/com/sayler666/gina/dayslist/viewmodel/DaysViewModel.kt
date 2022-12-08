package com.sayler666.gina.dayslist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.dayslist.usecase.GetDaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DaysViewModel @Inject constructor(
    getDaysUseCase: GetDaysUseCase,
    daysMapper: DaysMapper
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
}
