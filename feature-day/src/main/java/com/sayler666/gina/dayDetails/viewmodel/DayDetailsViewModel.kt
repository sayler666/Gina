package com.sayler666.gina.dayDetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.usecaase.GetNextPreviousIdDayUseCase
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.Back
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToAttachment
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToDayDetails
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToNextDay
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToPreviousDay
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.ShowSnackBar
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnAttachmentPressed
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnDayDetailsPressed
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnNextDayPressed
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnPreviousDayPressed
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnResume
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DayDetailsViewModel.Factory::class)
class DayDetailsViewModel @AssistedInject constructor(
    @Assisted val dayId: Int,
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val getNextPreviousIdDayUseCase: GetNextPreviousIdDayUseCase,
    private val getDayDetailsUseCase: GetDayDetailsUseCase,
) : ViewModel() {

    private val mutableViewState: MutableStateFlow<DayDetailsState?> = MutableStateFlow(null)
    val viewState: StateFlow<DayDetailsState?> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    @AssistedFactory
    interface Factory {
        fun create(dayId: Int): DayDetailsViewModel
    }

    init {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
    }

    private fun fetchDayDetails() {
        viewModelScope.launch {
            getDayDetailsUseCase.getDayDetails(dayId)
                .onSuccess { mutableViewState.emit(it.toState()) }
        }
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnResume -> fetchDayDetails()
            OnBackPressed -> mutableViewActions.trySend(Back)
            OnNextDayPressed -> goToNextDay()
            OnPreviousDayPressed -> goToPreviousDay()
            OnDayDetailsPressed -> mutableViewActions.trySend(NavToDayDetails(dayId))
            is OnAttachmentPressed -> mutableViewActions.trySend(NavToAttachment(event.attachmentId))
        }
    }

    private fun goToNextDay() {
        viewModelScope.launch {
            getNextPreviousIdDayUseCase.getNextDayId(dayId)
                .onSuccess { dayId ->
                    mutableViewActions.trySend(NavToNextDay(dayId))
                }
                .onFailure { error ->
                    mutableViewActions.trySend(ShowSnackBar(error.message.orEmpty()))
                }
        }
    }

    private fun goToPreviousDay() {
        viewModelScope.launch {
            getNextPreviousIdDayUseCase.getPreviousDayId(dayId)
                .onSuccess { dayId ->
                    mutableViewActions.trySend(NavToPreviousDay(dayId))
                }
                .onFailure { error ->
                    mutableViewActions.trySend(ShowSnackBar(error.message.orEmpty()))
                }
        }
    }

    sealed interface ViewEvent {
        data object OnResume : ViewEvent
        data object OnDayDetailsPressed : ViewEvent
        data object OnNextDayPressed : ViewEvent
        data object OnPreviousDayPressed : ViewEvent
        data object OnBackPressed : ViewEvent
        data class OnAttachmentPressed(val attachmentId: Int) : ViewEvent
    }

    sealed interface ViewAction {
        data class NavToAttachment(val attachmentId: Int) : ViewAction
        data class NavToDayDetails(val dayId: Int) : ViewAction
        data class NavToNextDay(val dayId: Int) : ViewAction
        data class NavToPreviousDay(val dayId: Int) : ViewAction
        data class ShowSnackBar(val message: String) : ViewAction
        data object Back : ViewAction
    }
}
