package com.sayler666.gina.dayDetails.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.flow.Event
import com.sayler666.core.flow.Event.Value
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.usecaase.GetNextPreviousDayUseCase
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayDetailsViewModel @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    private val getNextPreviousDayUseCase: GetNextPreviousDayUseCase,
    private val dayDetailsMapper: DayDetailsMapper,
    getDayDetailsUseCase: GetDayDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        viewModelScope.launch { databaseProvider.openSavedDB() }
    }

    private val navArgs: DayDetailsScreenNavArgs =
        DayDetailsScreenDestination.argsFrom(savedStateHandle)
    private val id: Int
        get() = navArgs.dayId

    private val _error = MutableSharedFlow<String?>()
    val error = _error.asSharedFlow()

    private val date = MutableStateFlow<Long>(-1)
    private val _goToDayId = MutableStateFlow<Int?>(null)
    val goToDayId: StateFlow<Event<Int>>
        get() = _goToDayId.filterNotNull().map(::Value).stateIn(
            viewModelScope, WhileSubscribed(500), Event.Empty
        )

    val day = getDayDetailsUseCase.getDayDetails(id).filterNotNull()
        .onEach { day -> day.day.date?.let { date.emit(it) } }.map(dayDetailsMapper::mapToVm)
        .stateIn(viewModelScope, WhileSubscribed(500), null)

    fun goToNextDay() {
        viewModelScope.launch {
            if (date.value > 0) {
                getNextPreviousDayUseCase.getNextDayAfterDate(date.value)
                    .onSuccess { _goToDayId.emit(it) }
                    .onFailure { _error.emit(it.message) }
            }
        }
    }

    fun goToPreviousDay() {
        viewModelScope.launch {
            if (date.value > 0) getNextPreviousDayUseCase.getPreviousDayBeforeDate(date.value)
                .onSuccess { _goToDayId.emit(it) }
                .onFailure { _error.emit(it.message) }
        }
    }
}
