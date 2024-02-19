package com.sayler666.gina.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.ginaApp.navigation.BottomNavigationVisibilityManager
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
    private val bottomNavigationVisibilityManager: BottomNavigationVisibilityManager,
    getDaysUseCase: GetDaysUseCase,
    daysMapper: CalendarMapper,
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

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnHideBottomBar -> bottomNavigationVisibilityManager.hide()
            OnShowBottomBar -> bottomNavigationVisibilityManager.show()
        }
    }

    sealed interface ViewEvent {
        // TODO add rest of the events
        data object OnHideBottomBar : ViewEvent
        data object OnShowBottomBar : ViewEvent
    }
}
