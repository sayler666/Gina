package com.sayler666.gina.day.addDay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.domain.model.journal.Friend
import com.sayler666.domain.model.journal.Mood
import com.sayler666.domain.model.quotes.Quote
import com.sayler666.gina.day.addDay.usecase.AddDayUseCase
import com.sayler666.gina.day.addDay.usecase.GetQuoteUseCase
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.Back
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.DaySaved
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.NavToAttachment
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.ReinitializeText
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.ShowAttachmentPicker
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.ShowDiscardDialog
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentPickerPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentRemove
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentsAdded
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnContentChanged
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnMoodChanged
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnRestoreWorkingCopyPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnSaveChangesPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnSetNewDate
import com.sayler666.gina.reminders.usecase.ReminderDismissUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel(assistedFactory = AddDayViewModel.Factory::class)
class AddDayViewModel @AssistedInject constructor(
    @Assisted val date: LocalDate?,
    private val addDayUseCase: AddDayUseCase,
    private val reminderDismissUseCase: ReminderDismissUseCase,
    private val session: AddDaySession,
    getQuoteUseCase: GetQuoteUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(date: LocalDate?): AddDayViewModel
    }

    private val mutableViewState = MutableStateFlow<AddDayState?>(null)
    val viewState: StateFlow<AddDayState?> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    private val quote = MutableStateFlow<Quote?>(null)

    init {
        session.initialize(viewModelScope, date)
        viewModelScope.launch { quote.value = getQuoteUseCase.getQuote() }
        observeViewState()
    }

    private fun observeViewState() {
        combine(session.day, session.hasWorkingCopy, quote) { day, hasWorkingCopy, quote ->
            day?.toAddDayState(
                hasWorkingCopy = hasWorkingCopy,
                quote = quote
            )
        }.filterNotNull()
            .onEach { mutableViewState.value = it }
            .launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnAttachmentPressed -> mutableViewActions.trySend(
                NavToAttachment(
                    event.image,
                    event.mimeType,
                    event.hidden,
                )
            )

            is OnAttachmentRemove -> session.removeAttachment(event.attachmentHash)
            OnRestoreWorkingCopyPressed -> restoreWorkingCopy()
            OnAttachmentPickerPressed -> mutableViewActions.trySend(ShowAttachmentPicker)
            is OnAttachmentsAdded -> session.addAttachments(event.attachments)
            is OnMoodChanged -> session.setMood(event.mood)
            OnSaveChangesPressed -> saveChanges()
            is OnSetNewDate -> session.setDate(event.date)
            is OnContentChanged -> session.setContent(event.content)
            OnBackPressed -> if (session.hasChanges()) {
                mutableViewActions.trySend(ShowDiscardDialog)
            } else {
                mutableViewActions.trySend(Back)
            }

            is ViewEvent.OnFriendsChanged -> session.setFriends(event.friends)
        }
    }

    private fun saveChanges() {
        reminderDismissUseCase.dismissReminderNotification()
        session.day.value?.let { day ->
            viewModelScope.launch {
                addDayUseCase.addDay(day)
                session.clearWorkingCopy()
                mutableViewActions.trySend(DaySaved)
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
        data class OnContentChanged(val content: String) : ViewEvent
        data object OnRestoreWorkingCopyPressed : ViewEvent
        data object OnSaveChangesPressed : ViewEvent
        data class OnMoodChanged(val mood: Mood) : ViewEvent
        data class OnSetNewDate(val date: LocalDate) : ViewEvent
        data object OnAttachmentPickerPressed : ViewEvent
        data class OnAttachmentRemove(val attachmentHash: Int) : ViewEvent
        data class OnAttachmentPressed(
            val image: ByteArray,
            val mimeType: String,
            val hidden: Boolean
        ) : ViewEvent

        data class OnAttachmentsAdded(val attachments: List<Pair<ByteArray, String>>) : ViewEvent
        data class OnFriendsChanged(val friends: List<Friend>) : ViewEvent
    }

    sealed interface ViewAction {
        data object ShowAttachmentPicker : ViewAction
        data class NavToAttachment(
            val image: ByteArray,
            val mimeType: String,
            val hidden: Boolean
        ) : ViewAction

        data object ShowDiscardDialog : ViewAction
        data object Back : ViewAction
        data object DaySaved : ViewAction
        data class ReinitializeText(val content: String) : ViewAction
    }
}
