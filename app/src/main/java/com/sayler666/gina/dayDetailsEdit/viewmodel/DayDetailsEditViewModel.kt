package com.sayler666.gina.dayDetailsEdit.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.file.isImageMimeType
import com.sayler666.core.image.ImageOptimization
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsMapper
import com.sayler666.gina.dayDetailsEdit.ui.DayDetailsEditScreenNavArgs
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteDayUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.EditDayUseCase
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.entity.Attachment
import com.sayler666.gina.db.entity.DayDetails
import com.sayler666.gina.db.entity.Friend
import com.sayler666.gina.destinations.DayDetailsEditScreenDestination
import com.sayler666.gina.friends.usecase.AddFriendUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCase
import com.sayler666.gina.mood.Mood
import com.sayler666.gina.settings.viewmodel.ImageOptimizationViewModel
import com.sayler666.gina.workinCopy.WorkingCopyStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class DayDetailsEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDayDetailsUseCase: GetDayDetailsUseCase,
    getAllFriendsUseCase: GetAllFriendsUseCase,
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val addFriendUseCase: AddFriendUseCase,
    private val dayDetailsMapper: DayDetailsMapper,
    private val editDayUseCase: EditDayUseCase,
    private val deleteDayUseCase: DeleteDayUseCase,
    private val imageOptimization: ImageOptimization,
    private val imageOptimizationViewModel: ImageOptimizationViewModel,
    private val workingCopyStorage: WorkingCopyStorage
) : ViewModel(), ImageOptimizationViewModel by imageOptimizationViewModel {

    init {
        with(imageOptimizationViewModel) { initialize() }
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }
    private val navArgs: DayDetailsEditScreenNavArgs =
        DayDetailsEditScreenDestination.argsFrom(savedStateHandle)
    private val id: Int
        get() = navArgs.dayId

    private val allFriends = getAllFriendsUseCase.getAllFriendsWithCount().stateIn(
        viewModelScope,
        WhileSubscribed(500),
        emptyList()
    )
    private val friendsSearchQuery: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _attachmentsToDelete: MutableStateFlow<MutableList<Attachment>> = MutableStateFlow(
        mutableListOf()
    )
    private val _tempDay: MutableStateFlow<DayDetails?> = MutableStateFlow(null)
    val tempDay: StateFlow<DayDetailsEntity?> = combine(
        _tempDay,
        allFriends,
        friendsSearchQuery
    ) { day, allFriends, friendsSearchQuery ->
        day?.let { dayDetailsMapper.mapToVm(it, allFriends, friendsSearchQuery) }
    }
        .filterNotNull()
        .stateIn(
            viewModelScope,
            WhileSubscribed(500),
            null
        )

    val day = combine(getDayDetailsUseCase.getDayDetailsFlow(id), allFriends, friendsSearchQuery)
    { day, allFriends, friendsSearchQuery ->
        if (_tempDay.value == null) _tempDay.value = day
        day?.let { dayDetailsMapper.mapToVm(it, allFriends, friendsSearchQuery) }
    }
        .filterNotNull()
        .stateIn(
            viewModelScope,
            WhileSubscribed(500),
            null
        )

    val changesExist: StateFlow<Boolean> = tempDay.map {
        if (it != null && day.value != null) it != day.value else false
    }.stateIn(
        viewModelScope,
        WhileSubscribed(500),
        false
    )

    private val _workingCopy: MutableStateFlow<String> = MutableStateFlow("")
    val hasWorkingCopy = workingCopyStorage.getTextContent().map {
        it?.let { _workingCopy.emit(it) }

        !it.isNullOrEmpty()
    }

    private val _reinitializeText = MutableSharedFlow<Unit>()
    val reinitializeText = _reinitializeText.asSharedFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    private val _navigateToList = MutableSharedFlow<Unit>()
    val navigateToList = _navigateToList.asSharedFlow()

    fun setNewContent(newContent: String) {
        val temp = _tempDay.value ?: return
        _tempDay.value = temp.copy(day = temp.day.copy(content = newContent))

        // create "Working Copy" with WorkingCopyStorage
        if (newContent.isNotBlank() && changesExist.value) {
            viewModelScope.launch {
                workingCopyStorage.store(newContent)
            }
        }
    }

    fun setNewDate(date: LocalDate) {
        val currentDay = _tempDay.value ?: return
        _tempDay.value =
            currentDay.copy(day = currentDay.day.copy(date = date))
    }

    fun setNewMood(mood: Mood) {
        val currentDay = _tempDay.value ?: return
        _tempDay.value = currentDay.copy(day = currentDay.day.copy(mood = mood))
    }

    fun removeAttachment(byteHashCode: Int) {
        val currentDay = _tempDay.value ?: return
        val newAttachments = currentDay.attachments
            .toMutableList()
            .also { attachments ->
                attachments.removeIf {
                    val same = it.content.hashCode() == byteHashCode

                    // mark for deletion only if attachment already stored in DB (has nonnull dayId)
                    if (it.dayId != null && same) _attachmentsToDelete.value.add(it)
                    return@removeIf same
                }
            }

        _tempDay.value = currentDay.copy(attachments = newAttachments)
    }

    fun addAttachments(attachments: List<Pair<ByteArray, String>>) {
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
                    _tempDay.update {
                        it?.copy(attachments = it.attachments + newAttachment)
                    }
                }
            }
        }
    }

    fun searchFriend(searchQuery: String) {
        friendsSearchQuery.update { searchQuery }
    }

    fun addNewFriend(friendName: String) {
        viewModelScope.launch(SupervisorJob() + exceptionHandler) {
            addFriendUseCase.addFriend(friendName)
        }
    }

    fun friendSelect(friendId: Int, selected: Boolean) {
        _tempDay.update { day ->
            val friendInContext: Friend = allFriends.value.find { it.friendId == friendId }?.let {
                Friend(it.friendId, it.friendName, it.friendAvatar)
            } ?: return
            when (selected) {
                true -> day?.copy(friends = day.friends + friendInContext)
                false -> day?.copy(friends = day.friends.filterNot { it.id == friendId })
            }
        }
    }

    fun saveChanges() {
        _tempDay.value?.let {
            viewModelScope.launch {
                editDayUseCase.updateDay(it, attachmentsToDelete = _attachmentsToDelete.value)
                // clear working copy
                workingCopyStorage.clear()
                _navigateBack.emit(Unit)
            }
        }
    }

    fun removeDay() {
        _tempDay.value?.let {
            viewModelScope.launch {
                deleteDayUseCase.deleteDay(it)
                _navigateToList.emit(Unit)
            }
        }
    }

    // used to optimize attachment size
    fun optimizeAttachment(attachmentHash: Int) {
        val currentDay = _tempDay.value ?: return
        val toOptimize = currentDay.attachments.first { it.content.hashCode() == attachmentHash }
        val newAttachments = currentDay.attachments
            .toMutableList()
            .also { attachments ->
                attachments.removeIf {
                    val same = it.content.hashCode() == attachmentHash

                    // mark for deletion only if attachment already stored in DB (has nonnull dayId)
                    if (it.dayId != null && same) _attachmentsToDelete.value.add(it)
                    return@removeIf same
                }
            }
        viewModelScope.launch {
            val bytes = imageOptimization.optimizeImage(toOptimize.content!!)
            val newAttachment = Attachment(
                dayId = null,
                content = bytes,
                mimeType = toOptimize.mimeType,
                id = null
            )

            _tempDay.update {
                it?.copy(attachments = newAttachments + newAttachment)
            }
        }
    }

    fun restoreWorkingCopy() {
        val temp = _tempDay.value ?: return
        if (_workingCopy.value.isNotEmpty()){
            _tempDay.value = temp.copy(day = temp.day.copy(content = _workingCopy.value))
            viewModelScope.launch {
                _reinitializeText.emit(Unit)
            }
        }
    }
}
