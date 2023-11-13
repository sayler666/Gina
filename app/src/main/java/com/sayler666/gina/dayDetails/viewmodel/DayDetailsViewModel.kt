package com.sayler666.gina.dayDetails.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.dayDetails.ui.Way
import com.sayler666.gina.dayDetails.ui.Way.NEXT
import com.sayler666.gina.dayDetails.ui.Way.PREVIOUS
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.usecaase.GetNextPreviousDayUseCase
import com.sayler666.gina.db.Day
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayDetailsViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val getNextPreviousDayUseCase: GetNextPreviousDayUseCase,
    private val dayDetailsMapper: DayDetailsMapper,
    getDayDetailsUseCase: GetDayDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
    }

    private val navArgs: DayDetailsScreenNavArgs =
        DayDetailsScreenDestination.argsFrom(savedStateHandle)
    private val id: Int
        get() = navArgs.dayId

    private val _error = MutableSharedFlow<String?>()
    val error = _error.asSharedFlow()

    private val _day = MutableStateFlow<Day?>(null)

    private val _goToDayId = MutableSharedFlow<Pair<Int, Way>>()
    val goToDayId = _goToDayId.asSharedFlow()

    val day = getDayDetailsUseCase.getDayDetails(id).filterNotNull()
        .onEach { day -> day.day.let { _day.emit(it) } }
        .map(dayDetailsMapper::mapToVm)
        .stateIn(viewModelScope, WhileSubscribed(500), null)

    fun goToNextDay() {
        viewModelScope.launch {
            _day.value?.let {
                getNextPreviousDayUseCase.getNextDayAfterDate(it)
                    .onSuccess { _goToDayId.emit(it to NEXT) }
                    .onFailure { _error.emit(it.message) }
            }
        }
    }

    fun goToPreviousDay() {
        viewModelScope.launch {
            _day.value?.let {
                getNextPreviousDayUseCase.getPreviousDayBeforeDate(it)
                    .onSuccess { _goToDayId.emit(it to PREVIOUS) }
                    .onFailure { _error.emit(it.message) }
            }
        }
    }
}
