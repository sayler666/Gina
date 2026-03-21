package com.sayler666.gina.day.dayDetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.day.dayDetails.usecase.GetDayDetailsUseCase
import com.sayler666.gina.day.dayDetails.usecase.GetNextPreviousIdDayUseCase
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.Back
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToAttachment
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToDayDetails
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToGameOfLife
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToNextDay
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToPreviousDay
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.ShowSnackBar
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnAttachmentPressed
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnDayDetailsPressed
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnGameOfLifePressed
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnNextDayPressed
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnPreviousDayPressed
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnResume
import com.sayler666.gina.feature.settings.SettingsStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DayDetailsViewModel.Factory::class)
class DayDetailsViewModel @AssistedInject constructor(
    @Assisted val dayId: Int,
    private val getNextPreviousIdDayUseCase: GetNextPreviousIdDayUseCase,
    private val getDayDetailsUseCase: GetDayDetailsUseCase,
    private val settingsStorage: SettingsStorage,
) : ViewModel() {

    private val mutableViewState: MutableStateFlow<DayDetailsState?> = MutableStateFlow(null)
    val viewState: StateFlow<DayDetailsState?> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    @AssistedFactory
    interface Factory {
        fun create(dayId: Int): DayDetailsViewModel
    }

    private fun fetchDayDetails() {
        viewModelScope.launch {
            val incognito = settingsStorage.getIncognitoModeFlow().first()
            getDayDetailsUseCase.getDayDetails(dayId)
                .onSuccess { mutableViewState.emit(it.toState(incognito)) }
        }
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnResume -> fetchDayDetails()
            OnBackPressed -> mutableViewActions.trySend(Back)
            OnNextDayPressed -> goToNextDay()
            OnPreviousDayPressed -> goToPreviousDay()
            OnDayDetailsPressed -> mutableViewActions.trySend(NavToDayDetails(dayId))
            OnGameOfLifePressed -> mutableViewActions.trySend(
                NavToGameOfLife(mutableViewState.value?.content.orEmpty())
            )
            is OnAttachmentPressed -> {
                val imageIds = mutableViewState.value
                    ?.attachments
                    ?.filterIsInstance<AttachmentState.AttachmentImageState>()
                    ?.mapNotNull { it.id }
                    ?: emptyList()
                mutableViewActions.trySend(NavToAttachment(event.attachmentId, dayId, imageIds))
            }
        }
    }

    private fun goToNextDay() {
        viewModelScope.launch {
            getNextPreviousIdDayUseCase.getNextDayId(dayId)
                .onSuccess { dayId -> mutableViewActions.trySend(NavToNextDay(dayId)) }
                .onFailure { error -> mutableViewActions.trySend(ShowSnackBar(error.message.orEmpty())) }
        }
    }

    private fun goToPreviousDay() {
        viewModelScope.launch {
            getNextPreviousIdDayUseCase.getPreviousDayId(dayId)
                .onSuccess { dayId -> mutableViewActions.trySend(NavToPreviousDay(dayId)) }
                .onFailure { error -> mutableViewActions.trySend(ShowSnackBar(error.message.orEmpty())) }
        }
    }

    sealed interface ViewEvent {
        data object OnResume : ViewEvent
        data object OnDayDetailsPressed : ViewEvent
        data object OnGameOfLifePressed : ViewEvent
        data object OnNextDayPressed : ViewEvent
        data object OnPreviousDayPressed : ViewEvent
        data object OnBackPressed : ViewEvent
        data class OnAttachmentPressed(val attachmentId: Int) : ViewEvent
    }

    sealed interface ViewAction {
        data class NavToAttachment(
            val attachmentId: Int,
            val dayId: Int,
            val attachmentIds: List<Int>
        ) : ViewAction

        data class NavToDayDetails(val dayId: Int) : ViewAction
        data class NavToGameOfLife(val content: String) : ViewAction
        data class NavToNextDay(val dayId: Int) : ViewAction
        data class NavToPreviousDay(val dayId: Int) : ViewAction
        data class ShowSnackBar(val message: String) : ViewAction
        data object Back : ViewAction
    }
}
