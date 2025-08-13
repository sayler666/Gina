package com.sayler666.gina.addDay.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.core.file.isImageMimeType
import com.sayler666.core.image.ImageOptimization
import com.sayler666.core.string.getTextWithoutHtml
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.domain.model.journal.Day
import com.sayler666.domain.model.journal.DayDetails
import com.sayler666.domain.model.journal.Friend
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.addDay.ui.AddDayScreenNavArgs
import com.sayler666.gina.addDay.ui.AddDayState
import com.sayler666.gina.addDay.usecase.AddDayUseCase
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewAction.Back
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewAction.NavToAttachment
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewAction.ShowAttachmentPicker
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewAction.ShowDiscardDialog
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAddNewFriend
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentPickerPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentRemove
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentsAdded
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnContentChanged
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnFriendPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnFriendSearchQueryChanged
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnImageCompressionToggled
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnImageQualityChanged
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnMoodChanged
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnRestoreWorkingCopyPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnSaveChangesPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnSetNewDate
import com.sayler666.gina.attachments.viewmodel.toState
import com.sayler666.gina.destinations.AddDayScreenDestination
import com.sayler666.gina.friends.usecase.AddFriendUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsByRecentUseCaseImpl
import com.sayler666.gina.friends.viewmodel.FriendsMapper
import com.sayler666.gina.quotes.QuotesRepository
import com.sayler666.gina.reminder.receiver.ReminderReceiver.Companion.REMINDER_NOTIFICATION_ID
import com.sayler666.gina.reminder.usecase.NotificationUseCase
import com.sayler666.gina.settings.viewmodel.ImageOptimizationViewModel
import com.sayler666.gina.workinCopy.WorkingCopyStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddDayViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val addFriendUseCase: AddFriendUseCase,
    private val addDayUseCase: AddDayUseCase,
    private val imageOptimization: ImageOptimization,
    private val imageOptimizationViewModel: ImageOptimizationViewModel,
    private val notificationUseCase: NotificationUseCase,
    private val workingCopyStorage: WorkingCopyStorage,
    private val friendsMapper: FriendsMapper,
    savedStateHandle: SavedStateHandle,
    getAllFriendsByRecentUseCase: GetAllFriendsByRecentUseCaseImpl,
    quotesRepository: QuotesRepository,
) : ViewModel(), ImageOptimizationViewModel by imageOptimizationViewModel {

    private val navArgs: AddDayScreenNavArgs = AddDayScreenDestination.argsFrom(savedStateHandle)

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    init {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
        with(imageOptimizationViewModel) { initialize() }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    private val date: LocalDate?
        get() = navArgs.date

    private val blankDay = DayDetails(
        day = Day(
            date = date ?: LocalDate.now(),
        ),
        attachments = emptyList(),
        friends = emptyList()
    )

    private val workingCopy = workingCopyStorage.getTextContent().map {
        it?.let { mutableWorkingCopy.emit(it) }
        it
    }
    private val quote = quotesRepository.latestTodayQuoteFlow()
    private val allFriends = getAllFriendsByRecentUseCase().stateIn(
        viewModelScope,
        WhileSubscribed(500),
        emptyList()
    )

    private val mutableWorkingCopy: MutableStateFlow<String> = MutableStateFlow("")
    private val mutableFriendsSearchQuery: MutableStateFlow<String?> = MutableStateFlow(null)
    private val mutableDay: MutableStateFlow<DayDetails?> = MutableStateFlow(blankDay)

    val viewState: StateFlow<AddDayState?> = combine(
        mutableDay,
        allFriends,
        mutableFriendsSearchQuery,
        quote,
        workingCopy,
    ) { day, allFriends, friendsSearchQuery, quote, workingCopy ->
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
                friendsAll = friendsMapper.mapToDayFriends(
                    it.friends,
                    allFriends,
                    friendsSearchQuery
                ),
                quote = quote,
                workingCopyExists = !workingCopy.isNullOrEmpty()
            )
        }
    }
        .filterNotNull()
        .stateIn(
            viewModelScope,
            WhileSubscribed(500),
            null
        )

    private val _reinitializeText = MutableSharedFlow<Unit>()
    val reinitializeText = _reinitializeText.asSharedFlow()

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnAttachmentPressed -> mutableViewActions.trySend(
                NavToAttachment(event.image, event.mimeType)
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

        // create "Working Copy" with WorkingCopyStorage
        if (newContent.isNotBlank()) {
            viewModelScope.launch {
                workingCopyStorage.store(newContent)
            }
        }
    }

    private fun setNewDate(date: LocalDate) {
        val currentDay = mutableDay.value ?: return
        mutableDay.value =
            currentDay.copy(day = currentDay.day.copy(date = date))
    }

    private fun setNewMood(mood: Mood) {
        val currentDay = mutableDay.value ?: return
        mutableDay.value = currentDay.copy(day = currentDay.day.copy(mood = mood))
    }

    private fun removeAttachment(byteHashCode: Int) {
        val currentDay = mutableDay.value ?: return
        val newAttachments = currentDay.attachments
            .toMutableList()
            .also { attachments ->
                attachments.removeIf { it.content.hashCode() == byteHashCode }
            }

        mutableDay.value = currentDay.copy(attachments = newAttachments)
    }

    private fun addAttachments(attachments: List<Pair<ByteArray, String>>) {
        viewModelScope.launch {
            attachments.forEach { (content, mimeType) ->
                launch(SupervisorJob() + exceptionHandler) {
                    val bytes = when {
                        mimeType.isImageMimeType() -> imageOptimization.optimizeImage(content)
                        else -> content
                    }

                    val newAttachment = Attachment(
                        dayId = null,
                        content = bytes,
                        mimeType = mimeType,
                        id = null
                    )
                    mutableDay.update {
                        it?.copy(attachments = it.attachments + newAttachment)
                    }
                }
            }
        }
    }

    private fun searchFriend(searchQuery: String) {
        mutableFriendsSearchQuery.update { searchQuery }
    }

    private fun addNewFriend(friendName: String) {
        viewModelScope.launch(SupervisorJob() + exceptionHandler) {
            addFriendUseCase.addFriend(friendName)
        }
    }

    private fun friendSelect(friendId: Int, selected: Boolean) {
        mutableDay.update { day ->
            val friendInContext: Friend = allFriends.value.find { it.friendId == friendId }?.let {
                Friend(
                    it.friendId,
                    it.friendName,
                    it.friendAvatar
                )
            } ?: return
            when (selected) {
                true -> day?.copy(friends = day.friends + friendInContext)
                false -> day?.copy(friends = day.friends.filterNot { it.id == friendId })
            }
        }
    }

    private fun saveChanges() {
        // hide reminder notification if shown
        notificationUseCase.hideNotificationById(REMINDER_NOTIFICATION_ID)

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
            mutableDay.value = temp.copy(day = temp.day.copy(content = mutableWorkingCopy.value))
            viewModelScope.launch {
                _reinitializeText.emit(Unit)
            }
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
    }
}
