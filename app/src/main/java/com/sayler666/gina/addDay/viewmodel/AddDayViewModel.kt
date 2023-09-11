package com.sayler666.gina.addDay.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.date.toEpochMilliseconds
import com.sayler666.core.file.isImageMimeType
import com.sayler666.core.html.getTextWithoutHtml
import com.sayler666.core.image.ImageOptimization
import com.sayler666.gina.addDay.ui.AddDayScreenNavArgs
import com.sayler666.gina.addDay.usecase.AddDayUseCase
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsMapper
import com.sayler666.gina.db.Attachment
import com.sayler666.gina.db.Day
import com.sayler666.gina.db.DayDetails
import com.sayler666.gina.db.Friend
import com.sayler666.gina.destinations.AddDayScreenDestination
import com.sayler666.gina.friends.usecase.AddFriendUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsUseCase
import com.sayler666.gina.quotes.QuotesRepository
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
import mood.Mood
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddDayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getAllFriendsUseCase: GetAllFriendsUseCase,
    quotesRepository: QuotesRepository,
    private val addFriendUseCase: AddFriendUseCase,
    private val dayDetailsMapper: DayDetailsMapper,
    private val addDayUseCase: AddDayUseCase,
    private val imageOptimization: ImageOptimization
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }
    private val navArgs: AddDayScreenNavArgs =
        AddDayScreenDestination.argsFrom(savedStateHandle)
    private val date: LocalDate?
        get() = navArgs.date

    val quote = quotesRepository.latestTodayQuoteFlow()

    private val blankDay = DayDetails(
        Day(
            id = null,
            date = (date?.atStartOfDay()?.toLocalDate() ?: LocalDate.now()).toEpochMilliseconds(),
            content = "",
            mood = Mood.EMPTY
        ), emptyList(), emptyList()
    )

    private val allFriends = getAllFriendsUseCase.getAllFriendsWithCount().stateIn(
        viewModelScope,
        WhileSubscribed(500),
        emptyList()
    )
    private val friendsSearchQuery: MutableStateFlow<String?> = MutableStateFlow(null)

    private val _tempDay: MutableStateFlow<DayDetails?> = MutableStateFlow(blankDay)
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

    val changesExist: StateFlow<Boolean> = _tempDay.map {
        it?.copy(day = it.day.copy(content = it.day.content?.getTextWithoutHtml())) != blankDay
    }.stateIn(
        viewModelScope,
        WhileSubscribed(500),
        false
    )
    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

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
        _tempDay.value = currentDay.copy(day = currentDay.day.copy(mood = mood))
    }

    fun removeAttachment(byteHashCode: Int) {
        val currentDay = _tempDay.value ?: return
        val newAttachments = currentDay.attachments
            .toMutableList()
            .also { attachments ->
                attachments.removeIf { it.content.hashCode() == byteHashCode }
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
                addDayUseCase.addDay(it)
                _navigateBack.emit(Unit)
            }
        }
    }
}
