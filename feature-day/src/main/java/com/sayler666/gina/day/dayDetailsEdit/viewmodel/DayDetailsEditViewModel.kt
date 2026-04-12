package com.sayler666.gina.day.dayDetailsEdit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.domain.model.journal.Friend
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.day.dayDetails.usecase.GetDayDetailsUseCase
import com.sayler666.gina.day.dayDetailsEdit.usecase.DeleteDayUseCase
import com.sayler666.gina.day.dayDetailsEdit.usecase.EditDayUseCase
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.Back
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.ChangesSaved
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.NavToList
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.OpenImagePreview
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.ReinitializeText
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.ShowAttachmentPicker
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.ShowDiscardDialog
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentOpen
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentOptimize
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentPickerPressed
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentRemove
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentsAdded
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnContentChanged
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnMoodChanged
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnRemoveDayPressed
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnRestoreWorkingCopyPressed
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnSaveChangesPressed
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnSetNewDate
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel(assistedFactory = DayDetailsEditViewModel.Factory::class)
class DayDetailsEditViewModel @AssistedInject constructor(
    @Assisted val dayId: Int,
    private val getDayDetailsUseCase: GetDayDetailsUseCase,
    private val editDayUseCase: EditDayUseCase,
    private val deleteDayUseCase: DeleteDayUseCase,
    private val session: EditDaySession,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(dayId: Int): DayDetailsEditViewModel
    }

    private val mutableViewState = MutableStateFlow<DayDetailsEditState?>(null)
    val viewState: StateFlow<DayDetailsEditState?> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    private val _initialFriends = MutableStateFlow<List<Friend>>(emptyList())
    val initialFriends: StateFlow<List<Friend>> = _initialFriends.asStateFlow()

    init {
        viewModelScope.launch {
            getDayDetailsUseCase.getDayDetails(dayId)
                .onSuccess { day ->
                    _initialFriends.value = day.friends
                    session.initialize(viewModelScope, day)
                    observeViewState()
                }
                .onFailure { mutableViewActions.trySend(Back) }
        }
    }

    private fun observeViewState() {
        combine(session.day, session.hasWorkingCopy) { day, hasWorkingCopy ->
            day?.toEditState()?.copy(
                changesExist = session.hasChanges(),
                hasWorkingCopy = hasWorkingCopy,
            )
        }.onEach { mutableViewState.value = it }.launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnBackPressed -> if (session.hasChanges()) {
                mutableViewActions.trySend(ShowDiscardDialog)
            } else {
                mutableViewActions.trySend(Back)
            }

            OnSaveChangesPressed -> saveChanges()
            OnRemoveDayPressed -> removeDay()
            OnRestoreWorkingCopyPressed -> restoreWorkingCopy()
            is OnContentChanged -> session.setContent(event.content)
            is OnMoodChanged -> session.setMood(event.mood)
            OnAttachmentPickerPressed -> mutableViewActions.trySend(ShowAttachmentPicker)
            is OnAttachmentRemove -> session.removeAttachment(event.attachmentHash)
            is OnAttachmentsAdded -> session.addAttachments(event.attachments)
            is OnSetNewDate -> session.setDate(event.date)
            is OnAttachmentOptimize -> session.optimizeAttachment(event.attachmentHash)
            is OnAttachmentOpen -> mutableViewActions.trySend(OpenImagePreview(event.attachment))
            is ViewEvent.OnFriendsChanged -> session.setFriends(event.friends)
        }
    }

    private fun saveChanges() {
        session.day.value?.let { day ->
            viewModelScope.launch {
                editDayUseCase.updateDay(day, attachmentsToDelete = session.attachmentsToDelete)
                session.clearWorkingCopy()
                mutableViewActions.trySend(ChangesSaved)
            }
        }
    }

    private fun removeDay() {
        session.day.value?.let { day ->
            viewModelScope.launch {
                deleteDayUseCase.deleteDay(day)
                mutableViewActions.trySend(NavToList)
            }
        }
    }

    private fun restoreWorkingCopy() {
        session.restoreWorkingCopy()?.let { content ->
            mutableViewActions.trySend(ReinitializeText(content))
        }
    }

    sealed interface ViewEvent {
        data object OnBackPressed : ViewEvent
        data object OnSaveChangesPressed : ViewEvent
        data object OnRemoveDayPressed : ViewEvent
        data object OnRestoreWorkingCopyPressed : ViewEvent
        data class OnContentChanged(val content: String) : ViewEvent
        data class OnMoodChanged(val mood: Mood) : ViewEvent
        data object OnAttachmentPickerPressed : ViewEvent
        data class OnAttachmentRemove(val attachmentHash: Int) : ViewEvent
        data class OnAttachmentsAdded(val attachments: List<Pair<ByteArray, String>>) : ViewEvent
        data class OnSetNewDate(val date: LocalDate) : ViewEvent
        data class OnAttachmentOptimize(val attachmentHash: Int) : ViewEvent
        data class OnAttachmentOpen(val attachment: AttachmentState) : ViewEvent
        data class OnFriendsChanged(val friends: List<Friend>) : ViewEvent
    }

    sealed interface ViewAction {
        data object Back : ViewAction
        data object ChangesSaved : ViewAction
        data object NavToList : ViewAction
        data object ShowAttachmentPicker : ViewAction
        data object ShowDiscardDialog : ViewAction
        data class OpenImagePreview(val attachmentState: AttachmentState) : ViewAction
        data class ReinitializeText(val content: String) : ViewAction
    }
}
