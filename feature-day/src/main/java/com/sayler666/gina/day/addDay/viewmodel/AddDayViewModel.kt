package com.sayler666.gina.day.addDay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.core.string.getTextWithoutHtml
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.domain.model.journal.Day
import com.sayler666.domain.model.journal.DayDetails
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.day.addDay.ui.AddDayState
import com.sayler666.gina.day.addDay.usecase.AddDayUseCase
import com.sayler666.gina.day.addDay.usecase.DayQuoteProvider
import com.sayler666.gina.day.addDay.usecase.ReminderDismissUseCase
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.Back
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.NavToAttachment
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.ReinitializeText
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.ShowAttachmentPicker
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.ShowDiscardDialog
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAddNewFriend
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentPickerPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentRemove
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentsAdded
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnContentChanged
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnFriendPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnFriendSearchQueryChanged
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnImageCompressionToggled
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnImageQualityChanged
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnMoodChanged
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnRestoreWorkingCopyPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnSaveChangesPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnSetNewDate
import com.sayler666.gina.day.attachments.viewmodel.toState
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayEditingViewModelSlice
import com.sayler666.gina.day.workinCopy.WorkingCopyStorage
import com.sayler666.gina.feature.settings.viewmodel.ImageOptimizationViewModel
import com.sayler666.gina.friends.viewmodel.FriendsMapper
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
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val addDayUseCase: AddDayUseCase,
    private val imageOptimizationViewModel: ImageOptimizationViewModel,
    private val reminderDismissUseCase: ReminderDismissUseCase,
    private val workingCopyStorage: WorkingCopyStorage,
    private val friendsMapper: FriendsMapper,
    private val dayEditingSlice: DayEditingViewModelSlice,
    dayQuoteProvider: DayQuoteProvider,
) : ViewModel(), ImageOptimizationViewModel by imageOptimizationViewModel,
    DayEditingViewModelSlice by dayEditingSlice {

    @AssistedFactory
    interface Factory {
        fun create(date: LocalDate?): AddDayViewModel
    }

    private val mutableViewState = MutableStateFlow<AddDayState?>(null)
    val viewState: StateFlow<AddDayState?> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    private val blankDay = DayDetails(
        day = Day(date = date ?: LocalDate.now()),
        attachments = emptyList(),
        friends = emptyList()
    )

    private val mutableWorkingCopy = MutableStateFlow("")
    private val quote = dayQuoteProvider.latestTodayQuoteFlow()

    init {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
        with(imageOptimizationViewModel) { initialize() }
        dayEditingSlice.initializeSlice(viewModelScope)
        mutableDay.value = DayDetails(
            day = Day(date = date ?: LocalDate.now()),
            attachments = emptyList(),
            friends = emptyList()
        )
        observeViewState()
        observeWorkingCopy()
    }

    private fun observeViewState() {
        combine(
            mutableDay,
            allFriends,
            friendsSearchQuery,
            quote,
            mutableWorkingCopy
        ) { day, friends, query, q, workingCopy ->
            day?.let {
                AddDayState(
                    id = it.day.id,
                    dayOfMonth = getDayOfMonth(it.day.date),
                    dayOfWeek = getDayOfWeek(it.day.date),
                    yearAndMonth = getYearAndMonth(it.day.date),
                    localDate = it.day.date,
                    content = it.day.content,
                    attachments = it.attachments.map(Attachment::toState),
                    mood = it.day.mood,
                    friendsAll = friendsMapper.mapToDayFriends(it.friends, friends, query),
                    quote = q,
                    workingCopyExists = workingCopy.isNotEmpty()
                )
            }
        }.filterNotNull()
            .onEach { mutableViewState.value = it }
            .launchIn(viewModelScope)
    }

    private fun observeWorkingCopy() {
        workingCopyStorage.getTextContent()
            .onEach { content -> content?.let { mutableWorkingCopy.value = it } }
            .launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnAttachmentPressed -> mutableViewActions.trySend(
                NavToAttachment(
                    event.image,
                    event.mimeType
                )
            )

            is OnAttachmentRemove -> removeAttachment(event.attachmentHash)
            OnRestoreWorkingCopyPressed -> restoreWorkingCopy()
            is OnAddNewFriend -> addNewFriend(event.name)
            OnAttachmentPickerPressed -> mutableViewActions.trySend(ShowAttachmentPicker)
            is OnAttachmentsAdded -> addAttachments(event.attachments)
            is OnFriendPressed -> friendSelect(event.friendId, event.selected)
            is OnFriendSearchQueryChanged -> searchFriend(event.query)
            is OnMoodChanged -> setNewMood(event.mood)
            OnSaveChangesPressed -> saveChanges()
            is OnSetNewDate -> setNewDate(event.date)
            is OnContentChanged -> setNewContent(event.content)
            is OnImageCompressionToggled -> imageOptimizationViewModel.toggleImageCompression(event.enabled)
            is OnImageQualityChanged -> imageOptimizationViewModel.setNewImageQuality(event.quality)
            OnBackPressed -> if (changesExists()) {
                mutableViewActions.trySend(ShowDiscardDialog)
            } else {
                mutableViewActions.trySend(Back)
            }
        }
    }

    private fun changesExists(): Boolean {
        val dayDetails = mutableDay.value
        return !(dayDetails == null || dayDetails.copy(day = dayDetails.day.copy(content = dayDetails.day.content.getTextWithoutHtml())) == blankDay)
    }

    private fun setNewContent(newContent: String) {
        val temp = mutableDay.value ?: return
        mutableDay.value = temp.copy(day = temp.day.copy(content = newContent))

        if (newContent.isNotBlank()) {
            viewModelScope.launch { workingCopyStorage.store(newContent) }
        }
    }

    private fun saveChanges() {
        reminderDismissUseCase.dismissReminderNotification()
        mutableDay.value?.let {
            viewModelScope.launch {
                addDayUseCase.addDay(it)
                workingCopyStorage.clear()
                mutableViewActions.trySend(Back)
            }
        }
    }

    private fun restoreWorkingCopy() {
        val temp = mutableDay.value ?: return
        if (mutableWorkingCopy.value.isNotEmpty()) {
            val content = mutableWorkingCopy.value
            mutableDay.value = temp.copy(day = temp.day.copy(content = content))
            mutableViewActions.trySend(ReinitializeText(content))
        }
    }

    sealed interface ViewEvent {
        data object OnBackPressed : ViewEvent
        data class OnContentChanged(val content: String) : ViewEvent
        data object OnRestoreWorkingCopyPressed : ViewEvent
        data object OnSaveChangesPressed : ViewEvent
        data class OnMoodChanged(val mood: Mood) : ViewEvent
        data class OnFriendSearchQueryChanged(val query: String) : ViewEvent
        data class OnSetNewDate(val date: LocalDate) : ViewEvent
        data class OnAddNewFriend(val name: String) : ViewEvent
        data class OnFriendPressed(val friendId: Int, val selected: Boolean) : ViewEvent
        data object OnAttachmentPickerPressed : ViewEvent
        data class OnAttachmentRemove(val attachmentHash: Int) : ViewEvent
        data class OnAttachmentPressed(val image: ByteArray, val mimeType: String) : ViewEvent
        data class OnAttachmentsAdded(val attachments: List<Pair<ByteArray, String>>) : ViewEvent
        data class OnImageQualityChanged(val quality: Int) : ViewEvent
        data class OnImageCompressionToggled(val enabled: Boolean) : ViewEvent
    }

    sealed interface ViewAction {
        data object ShowAttachmentPicker : ViewAction
        data class NavToAttachment(val image: ByteArray, val mimeType: String) : ViewAction
        data object ShowDiscardDialog : ViewAction
        data object Back : ViewAction
        data class ReinitializeText(val content: String) : ViewAction
    }
}
