package com.sayler666.gina.dayDetails.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCase
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DayDetailsViewModel @Inject constructor(
    getDayDetailsUseCase: GetDayDetailsUseCase,
    dayDetailsMapper: DayDetailsMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navArgs: DayDetailsScreenNavArgs = DayDetailsScreenDestination.argsFrom(savedStateHandle)
    private val id: Int
        get() = navArgs.dayId

    val day = getDayDetailsUseCase
        .getDayDetails(id)
        .map(dayDetailsMapper::mapToVm)
        .stateIn(
            viewModelScope,
            WhileSubscribed(500),
            null
        )
}
