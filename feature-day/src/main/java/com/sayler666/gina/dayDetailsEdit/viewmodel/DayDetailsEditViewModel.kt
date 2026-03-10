package com.sayler666.gina.dayDetailsEdit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.image.ImageOptimization
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.dayDetails.usecase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetails.viewmodel.toEditState
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteDayUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.EditDayUseCase
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.Back
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.NavToList
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.ReinitializeText
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.ShowAttachmentPicker
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.ShowDiscardDialog
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentOptimize
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentPickerPressed
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentRemove
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentsAdded
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnContentChanged
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnFriendPressed
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnFriendSearchQueryChanged
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnImageCompressionToggled
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnImageQualityChanged
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnMoodChanged
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnRemoveDayPressed
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnRestoreWorkingCopyPressed
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnSaveChangesPressed
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnSetNewDate
import com.sayler666.gina.feature.settings.viewmodel.ImageOptimizationViewModel
import com.sayler666.gina.friends.viewmodel.FriendsMapper
import com.sayler666.gina.workinCopy.WorkingCopyStorage
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel(assistedFactory = DayDetailsEditViewModel.Factory::class)
class DayDetailsEditViewModel @AssistedInject constructor(
    @Assisted val dayId: Int,
    private val getDayDetailsUseCase: GetDayDetailsUseCase,
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val friendsMapper: FriendsMapper,
    private val editDayUseCase: EditDayUseCase,
    private val deleteDayUseCase: DeleteDayUseCase,
    private val imageOptimization: ImageOptimization,
    private val imageOptimizationViewModel: ImageOptimizationViewModel,
    private val workingCopyStorage: WorkingCopyStorage,
    private val dayEditingSlice: DayEditingViewModelSlice,
) : ViewModel(), ImageOptimizationViewModel by imageOptimizationViewModel,
    DayEditingViewModelSlice by dayEditingSlice {

    @AssistedFactory
    interface Factory {
        fun create(dayId: Int): DayDetailsEditViewModel
    }

    private val mutableViewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = mutableViewState.asStateFlow()

    private val mutableViewActions = Channel<ViewAction>(Channel.BUFFERED)
    val viewActions = mutableViewActions.receiveAsFlow()

    private val _attachmentsToDelete: MutableStateFlow<MutableList<Attachment>> =
        MutableStateFlow(mutableListOf())

    private val _workingCopy = MutableStateFlow("")

    // Latest values used to compute changesExist without intermediate StateFlows
    private var latestStoredDay: DayDetailsEntity? = null
    private var latestTempDay: DayDetailsEntity? = null

    init {
        with(imageOptimizationViewModel) { initialize() }
        dayEditingSlice.initializeSlice(viewModelScope)
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
        observeStoredDay()
        observeTempDay()
        observeWorkingCopy()
    }

    private fun observeStoredDay() {
        combine(
            getDayDetailsUseCase.getDayDetailsFlow(dayId),
            allFriends,
            friendsSearchQuery
        ) { day, friends, query ->
            if (mutableDay.value == null) mutableDay.value = day
            day?.toEditState(friendsMapper, friends, query)
        }.onEach { stored ->
            latestStoredDay = stored
            updateCurrentDayState()
        }.launchIn(viewModelScope)
    }

    private fun observeTempDay() {
        combine(mutableDay, allFriends, friendsSearchQuery) { day, friends, query ->
            day?.toEditState(friendsMapper, friends, query)
        }.onEach { temp ->
            latestTempDay = temp
            updateCurrentDayState()
        }.launchIn(viewModelScope)
    }

    private fun updateCurrentDayState() {
        val stored = latestStoredDay
        val temp = latestTempDay
        val current = temp ?: stored
        val changesExist = temp != null && stored != null && temp != stored
        mutableViewState.update { it.copy(currentDay = current, changesExist = changesExist) }
    }

    private fun observeWorkingCopy() {
        workingCopyStorage.getTextContent().onEach { content ->
            content?.let { _workingCopy.value = it }
            mutableViewState.update { it.copy(hasWorkingCopy = !content.isNullOrEmpty()) }
        }.launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            OnBackPressed -> if (viewState.value.changesExist) {
                mutableViewActions.trySend(ShowDiscardDialog)
            } else {
                mutableViewActions.trySend(Back)
            }

            OnSaveChangesPressed -> saveChanges()
            OnRemoveDayPressed -> removeDay()
            OnRestoreWorkingCopyPressed -> restoreWorkingCopy()
            is OnContentChanged -> setNewContent(event.content)
            is OnMoodChanged -> setNewMood(event.mood)
            is OnFriendSearchQueryChanged -> searchFriend(event.query)
            is ViewEvent.OnAddNewFriend -> addNewFriend(event.name)
            is OnFriendPressed -> friendSelect(event.friendId, event.selected)
            OnAttachmentPickerPressed -> mutableViewActions.trySend(ShowAttachmentPicker)
            is OnAttachmentRemove -> removeAttachment(event.attachmentHash)
            is OnAttachmentsAdded -> addAttachments(event.attachments)
            is OnSetNewDate -> setNewDate(event.date)
            is OnImageQualityChanged -> imageOptimizationViewModel.setNewImageQuality(event.quality)
            is OnImageCompressionToggled -> imageOptimizationViewModel.toggleImageCompression(event.enabled)
            is OnAttachmentOptimize -> optimizeAttachment(event.attachmentHash)
        }
    }

    private fun setNewContent(newContent: String) {
        val temp = mutableDay.value ?: return
        mutableDay.value = temp.copy(day = temp.day.copy(content = newContent))

        if (newContent.isNotBlank() && viewState.value.changesExist) {
            viewModelScope.launch { workingCopyStorage.store(newContent) }
        }
    }

    // override to also track attachments that need DB deletion
    override fun removeAttachment(byteHashCode: Int) {
        mutableDay.value?.attachments
            ?.firstOrNull { it.content.hashCode() == byteHashCode && it.dayId != null }
            ?.let { _attachmentsToDelete.value.add(it) }
        dayEditingSlice.removeAttachment(byteHashCode)
    }

    private fun saveChanges() {
        mutableDay.value?.let {
            viewModelScope.launch {
                editDayUseCase.updateDay(it, attachmentsToDelete = _attachmentsToDelete.value)
                workingCopyStorage.clear()
                mutableViewActions.trySend(Back)
            }
        }
    }

    private fun removeDay() {
        mutableDay.value?.let {
            viewModelScope.launch {
                deleteDayUseCase.deleteDay(it)
                mutableViewActions.trySend(NavToList)
            }
        }
    }

    private fun optimizeAttachment(attachmentHash: Int) {
        val currentDay = mutableDay.value ?: return
        val toOptimize = currentDay.attachments.first { it.content.hashCode() == attachmentHash }
        val newAttachments = currentDay.attachments
            .toMutableList()
            .also { attachments ->
                attachments.removeIf {
                    val same = it.content.hashCode() == attachmentHash
                    if (it.dayId != null && same) _attachmentsToDelete.value.add(it)
                    return@removeIf same
                }
            }
        viewModelScope.launch {
            val bytes = imageOptimization.optimizeImage(toOptimize.content)
            val newAttachment = Attachment(
                dayId = null,
                content = bytes,
                mimeType = toOptimize.mimeType,
                id = null
            )
            mutableDay.update { it?.copy(attachments = newAttachments + newAttachment) }
        }
    }

    private fun restoreWorkingCopy() {
        val temp = mutableDay.value ?: return
        if (_workingCopy.value.isNotEmpty()) {
            val content = _workingCopy.value
            mutableDay.value = temp.copy(day = temp.day.copy(content = content))
            mutableViewActions.trySend(ReinitializeText(content))
        }
    }

    data class ViewState(
        val currentDay: DayDetailsEntity? = null,
        val changesExist: Boolean = false,
        val hasWorkingCopy: Boolean = false,
    )

    sealed interface ViewEvent {
        data object OnBackPressed : ViewEvent
        data object OnSaveChangesPressed : ViewEvent
        data object OnRemoveDayPressed : ViewEvent
        data object OnRestoreWorkingCopyPressed : ViewEvent
        data class OnContentChanged(val content: String) : ViewEvent
        data class OnMoodChanged(val mood: Mood) : ViewEvent
        data class OnFriendSearchQueryChanged(val query: String) : ViewEvent
        data class OnAddNewFriend(val name: String) : ViewEvent
        data class OnFriendPressed(val friendId: Int, val selected: Boolean) : ViewEvent
        data object OnAttachmentPickerPressed : ViewEvent
        data class OnAttachmentRemove(val attachmentHash: Int) : ViewEvent
        data class OnAttachmentsAdded(val attachments: List<Pair<ByteArray, String>>) : ViewEvent
        data class OnSetNewDate(val date: LocalDate) : ViewEvent
        data class OnImageQualityChanged(val quality: Int) : ViewEvent
        data class OnImageCompressionToggled(val enabled: Boolean) : ViewEvent
        data class OnAttachmentOptimize(val attachmentHash: Int) : ViewEvent
    }

    sealed interface ViewAction {
        data object Back : ViewAction
        data object NavToList : ViewAction
        data object ShowAttachmentPicker : ViewAction
        data object ShowDiscardDialog : ViewAction
        data class ReinitializeText(val content: String) : ViewAction
    }
}
