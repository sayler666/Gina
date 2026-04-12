package com.sayler666.gina.day.addDay.viewmodel

import com.sayler666.core.file.isImageMimeType
import com.sayler666.core.image.ImageOptimization
import com.sayler666.core.string.getTextWithoutHtml
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.domain.model.journal.Day
import com.sayler666.domain.model.journal.DayDetails
import com.sayler666.domain.model.journal.Friend
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.day.dayDetailsEdit.usecase.TmpAttachmentHiddenStore
import com.sayler666.gina.day.workinCopy.WorkingCopyStorage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class AddDaySession @Inject constructor(
    private val imageOptimization: ImageOptimization,
    private val workingCopyStorage: WorkingCopyStorage,
    private val tmpAttachmentHiddenStore: TmpAttachmentHiddenStore,
) {
    private val _day = MutableStateFlow<DayDetails?>(null)
    val day: StateFlow<DayDetails?> = _day.asStateFlow()

    private var blankDay: DayDetails? = null

    private val _workingCopy = MutableStateFlow("")
    private val _hasWorkingCopy = MutableStateFlow(false)
    val hasWorkingCopy: StateFlow<Boolean> = _hasWorkingCopy.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, e -> Timber.e(e) }

    private lateinit var scope: CoroutineScope

    fun initialize(scope: CoroutineScope, date: LocalDate?) {
        if (blankDay != null) return
        this.scope = scope
        val day = DayDetails(
            day = Day(date = date ?: LocalDate.now()),
            attachments = emptyList(),
            friends = emptyList()
        )
        blankDay = day
        _day.value = day
        observeWorkingCopy()
        observeTmpHiddenChanges()
    }

    fun hasChanges(): Boolean {
        val current = _day.value ?: return false
        val blank = blankDay ?: return false
        return current.copy(
            day = current.day.copy(content = current.day.content.getTextWithoutHtml())
        ) != blank
    }

    fun setContent(content: String) {
        val current = _day.value ?: return
        _day.value = current.copy(day = current.day.copy(content = content))
        if (content.isNotBlank()) {
            scope.launch { workingCopyStorage.store(content) }
        }
    }

    fun setMood(mood: Mood) {
        val current = _day.value ?: return
        _day.value = current.copy(day = current.day.copy(mood = mood))
    }

    fun setDate(date: LocalDate) {
        val current = _day.value ?: return
        _day.value = current.copy(day = current.day.copy(date = date))
    }

    fun setFriends(friends: List<Friend>) {
        val current = _day.value ?: return
        _day.value = current.copy(friends = friends)
    }

    fun addAttachments(attachments: List<Pair<ByteArray, String>>) {
        scope.launch {
            supervisorScope {
                attachments.forEach { (content, mimeType) ->
                    launch(exceptionHandler) {
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
                        _day.update { it?.copy(attachments = it.attachments + newAttachment) }
                    }
                }
            }
        }
    }

    fun removeAttachment(byteHashCode: Int) {
        val current = _day.value ?: return
        _day.value = current.copy(
            attachments = current.attachments.filter { it.content.hashCode() != byteHashCode }
        )
    }

    fun restoreWorkingCopy(): String? {
        val content = _workingCopy.value.takeIf { it.isNotEmpty() } ?: return null
        val current = _day.value ?: return null
        _day.value = current.copy(day = current.day.copy(content = content))
        return content
    }

    suspend fun clearWorkingCopy() {
        workingCopyStorage.clear()
    }

    private fun observeWorkingCopy() {
        workingCopyStorage.getTextContent().onEach { content ->
            content?.let { _workingCopy.value = it }
            _hasWorkingCopy.value = !content.isNullOrEmpty()
        }.launchIn(scope)
    }

    private fun observeTmpHiddenChanges() {
        tmpAttachmentHiddenStore.updates.onEach { (contentHash, hidden) ->
            _day.update { day ->
                day?.copy(attachments = day.attachments.map { attachment ->
                    if (attachment.content.contentHashCode() == contentHash)
                        attachment.copy(hidden = hidden)
                    else attachment
                })
            }
        }.launchIn(scope)
    }
}
