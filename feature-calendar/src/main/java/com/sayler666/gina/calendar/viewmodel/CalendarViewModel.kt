package com.sayler666.gina.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.navigation.BottomNavigationVisibilityManager
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.data.database.db.journal.usecase.GetDaysUseCase
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewAction.NavToAddDay
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewAction.NavToDayDetails
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnDayClick
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnEmptyDayClick
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnShowBottomBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val bottomNavigationVisibilityManager: BottomNavigationVisibilityManager,
    private val getDaysUseCase: GetDaysUseCase,
    private val daysMapper: CalendarMapper,
) : ViewModel() {

    private val mutableViewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    init {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
        observeDays()
    }

    private fun observeDays() {
        getDaysUseCase.getAllDaysFlow()
            .map { daysMapper.mapToVm(it) }
            .onEach { days -> mutableViewState.update { it.copy(days = days) } }
            .launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnHideBottomBar -> bottomNavigationVisibilityManager.hide()
            OnShowBottomBar -> bottomNavigationVisibilityManager.show()
            is OnDayClick -> mutableViewActions.trySend(NavToDayDetails(event.day.id))
            is OnEmptyDayClick -> mutableViewActions.trySend(NavToAddDay(event.date))
        }
    }

    data class ViewState(
        val days: List<CalendarDayEntity> = emptyList()
    )

    sealed interface ViewEvent {
        data object OnHideBottomBar : ViewEvent
        data object OnShowBottomBar : ViewEvent
        data class OnDayClick(val day: CalendarDayEntity) : ViewEvent
        data class OnEmptyDayClick(val date: LocalDate) : ViewEvent
    }

    sealed interface ViewAction {
        data class NavToDayDetails(val dayId: Int) : ViewAction
        data class NavToAddDay(val date: LocalDate) : ViewAction
    }
}
