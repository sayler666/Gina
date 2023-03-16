package com.sayler666.gina.dayDetailsEdit.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.core.date.toEpochMilliseconds
import com.sayler666.gina.core.file.isImageMimeType
import com.sayler666.gina.core.flow.Event
import com.sayler666.gina.dayDetails.usecaase.GetAllFriendsUseCase
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsMapper
import com.sayler666.gina.dayDetailsEdit.ui.DayDetailsEditScreenNavArgs
import com.sayler666.gina.dayDetailsEdit.usecase.AddFriendUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteDayUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.EditDayUseCase
import com.sayler666.gina.db.Attachment
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.DayDetails
import com.sayler666.gina.destinations.DayDetailsEditScreenDestination
import com.sayler666.gina.imageCompressor.ImageCompressor
import com.sayler666.gina.ui.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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
    private val databaseProvider: DatabaseProvider,
    private val addFriendUseCase: AddFriendUseCase,
    private val dayDetailsMapper: DayDetailsMapper,
    private val editDayUseCase: EditDayUseCase,
    private val deleteDayUseCase: DeleteDayUseCase,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    init {
        viewModelScope.launch { databaseProvider.openSavedDB() }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }
    private val navArgs: DayDetailsEditScreenNavArgs =
        DayDetailsEditScreenDestination.argsFrom(savedStateHandle)
    private val id: Int
        get() = navArgs.dayId

    private val allFriends = getAllFriendsUseCase.getAllFriends().stateIn(
        viewModelScope,
        WhileSubscribed(500),
        emptyList()
    )
    private val friendsSearchQuery: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _attachmentsToDelete: MutableStateFlow<MutableList<Attachment>> = MutableStateFlow(
        mutableListOf()
    )
    private val _tempDay: MutableStateFlow<DayDetails?> = MutableStateFlow(null)
    val tempDay: StateFlow<DayDetailsEntity?>
        get() = combine(
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

    val day = combine(getDayDetailsUseCase.getDayDetails(id), allFriends, friendsSearchQuery)
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

    val changesExist: StateFlow<Boolean> = tempDay.flatMapLatest {
        flow {
            if (it != null && day.value != null) emit(it != day.value)
        }
    }.stateIn(
        viewModelScope,
        WhileSubscribed(500),
        false
    )

    private val _navigateBack: MutableStateFlow<Event<Unit>> = MutableStateFlow(Event.Empty)
    val navigateBack: StateFlow<Event<Unit>>
        get() = _navigateBack

    private val _navigateToList: MutableStateFlow<Event<Unit>> = MutableStateFlow(Event.Empty)
    val navigateToList: StateFlow<Event<Unit>>
        get() = _navigateToList

    fun setNewContent(newContent: String) {
        val temp = _tempDay.value ?: return
        _tempDay.value = temp.copy(day = temp.day.copy(content = newContent))
    }

    fun setNewDate(date: LocalDate) {
        val currentDay = _tempDay.value ?: return
        _tempDay.value =
            currentDay.copy(day = currentDay.day.copy(date = date.toEpochMilliseconds()))
    }

    fun setNewMood(mood: Mood) {
        val currentDay = _tempDay.value ?: return
        _tempDay.value = currentDay.copy(day = currentDay.day.copy(mood = mood.numberValue))
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
                        mimeType.isImageMimeType() -> imageCompressor.compressImage(content)
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
            val friendInContext = allFriends.value.find { it.id == friendId } ?: return
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
                _navigateBack.tryEmit(Event.Value(Unit))
            }
        }
    }

    fun removeDay() {
        _tempDay.value?.let {
            viewModelScope.launch {
                deleteDayUseCase.deleteDay(it)
                _navigateToList.tryEmit(Event.Value(Unit))
            }
        }
    }
}
