package com.sayler666.gina.day.dayDetailsEdit.viewmodel

import com.sayler666.core.file.isImageMimeType
import com.sayler666.core.image.ImageOptimization
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.domain.model.journal.DayDetails
import com.sayler666.domain.model.journal.Friend
import com.sayler666.domain.model.journal.FriendWithCount
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.friends.usecase.AddFriendUseCase
import com.sayler666.gina.friends.usecase.GetAllFriendsByRecentUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

interface DayEditingViewModelSlice {
    val mutableDay: MutableStateFlow<DayDetails?>
    val allFriends: StateFlow<List<FriendWithCount>>
    val friendsSearchQuery: MutableStateFlow<String?>
    val exceptionHandler: CoroutineExceptionHandler

    fun initializeSlice(scope: CoroutineScope)
    fun addAttachments(attachments: List<Pair<ByteArray, String>>)
    fun removeAttachment(byteHashCode: Int)
    fun searchFriend(searchQuery: String)
    fun addNewFriend(friendName: String)
    fun friendSelect(friendId: Int, selected: Boolean)
    fun setNewMood(mood: Mood)
    fun setNewDate(date: LocalDate)
}

class DayEditingViewModelSliceImpl @Inject constructor(
    private val getAllFriendsByRecentUseCase: GetAllFriendsByRecentUseCase,
    private val imageOptimization: ImageOptimization,
    private val addFriendUseCase: AddFriendUseCase,
) : DayEditingViewModelSlice {

    private lateinit var sliceScope: CoroutineScope

    override val mutableDay: MutableStateFlow<DayDetails?> = MutableStateFlow(null)
    override val friendsSearchQuery: MutableStateFlow<String?> = MutableStateFlow(null)
    override val exceptionHandler = CoroutineExceptionHandler { _, exception -> Timber.e(exception) }
    override val allFriends: StateFlow<List<FriendWithCount>> by lazy {
        getAllFriendsByRecentUseCase().stateIn(
            sliceScope,
            WhileSubscribed(500),
            emptyList()
        )
    }

    override fun initializeSlice(scope: CoroutineScope) {
        sliceScope = scope
    }

    override fun addAttachments(attachments: List<Pair<ByteArray, String>>) {
        sliceScope.launch {
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
                    mutableDay.update { it?.copy(attachments = it.attachments + newAttachment) }
                }
            }
        }
    }

    override fun removeAttachment(byteHashCode: Int) {
        val currentDay = mutableDay.value ?: return
        val newAttachments = currentDay.attachments
            .toMutableList()
            .also { it.removeIf { a -> a.content.hashCode() == byteHashCode } }
        mutableDay.value = currentDay.copy(attachments = newAttachments)
    }

    override fun searchFriend(searchQuery: String) {
        friendsSearchQuery.update { searchQuery }
    }

    override fun addNewFriend(friendName: String) {
        sliceScope.launch(SupervisorJob() + exceptionHandler) {
            addFriendUseCase.addFriend(friendName)
        }
    }

    override fun friendSelect(friendId: Int, selected: Boolean) {
        mutableDay.update { day ->
            val friendInContext = allFriends.value.find { it.friendId == friendId }?.let {
                Friend(it.friendId, it.friendName, it.friendAvatar)
            } ?: return
            when (selected) {
                true -> day?.copy(friends = day.friends + friendInContext)
                false -> day?.copy(friends = day.friends.filterNot { it.id == friendId })
            }
        }
    }

    override fun setNewMood(mood: Mood) {
        val currentDay = mutableDay.value ?: return
        mutableDay.value = currentDay.copy(day = currentDay.day.copy(mood = mood))
    }

    override fun setNewDate(date: LocalDate) {
        val currentDay = mutableDay.value ?: return
        mutableDay.value = currentDay.copy(day = currentDay.day.copy(date = date))
    }
}
