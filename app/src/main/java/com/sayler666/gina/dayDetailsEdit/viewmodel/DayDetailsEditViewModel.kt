package com.sayler666.gina.dayDetailsEdit.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.core.date.toEpochMilliseconds
import com.sayler666.gina.core.flow.Event
import com.sayler666.gina.dayDetails.usecaase.GetDayDetailsUseCase
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsMapper
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsMapper.Companion.IMAGE_MIME_TYPE_PREFIX
import com.sayler666.gina.dayDetails.viewmodel.DayWithAttachmentsEntity
import com.sayler666.gina.dayDetailsEdit.ui.DayDetailsEditScreenNavArgs
import com.sayler666.gina.dayDetailsEdit.usecase.DeleteDayUseCase
import com.sayler666.gina.dayDetailsEdit.usecase.EditDayUseCase
import com.sayler666.gina.db.Attachment
import com.sayler666.gina.db.DayWithAttachment
import com.sayler666.gina.destinations.DayDetailsEditScreenDestination
import com.sayler666.gina.imageCompressor.ImageCompressor
import com.sayler666.gina.ui.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class DayDetailsEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDayDetailsUseCase: GetDayDetailsUseCase,
    private val dayDetailsMapper: DayDetailsMapper,
    private val editDayUseCase: EditDayUseCase,
    private val deleteDayUseCase: DeleteDayUseCase,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val navArgs: DayDetailsEditScreenNavArgs =
        DayDetailsEditScreenDestination.argsFrom(savedStateHandle)
    private val id: Int
        get() = navArgs.dayId

    private val _attachmentsToDelete: MutableStateFlow<MutableList<Attachment>> = MutableStateFlow(
        mutableListOf()
    )
    private val _tempDay: MutableStateFlow<DayWithAttachment?> = MutableStateFlow(null)
    val tempDay: StateFlow<DayWithAttachmentsEntity?>
        get() = _tempDay
            .filterNotNull()
            .map(dayDetailsMapper::mapToVm)
            .stateIn(
                viewModelScope,
                WhileSubscribed(500),
                null
            )

    val day = getDayDetailsUseCase
        .getDayDetails(id)
        .filterNotNull()
        .map {
            if (_tempDay.value == null) _tempDay.value = it
            dayDetailsMapper.mapToVm(it)
        }
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
        val currentDay = _tempDay.value ?: return
        viewModelScope.launch {
            val newAttachments = currentDay.attachments
                .toMutableList()
                .also {
                    attachments.forEach { (content, mimeType) ->
                        val bytes = if (mimeType.contains(IMAGE_MIME_TYPE_PREFIX)) {
                            imageCompressor.compressImage(content)
                        } else {
                            content
                        }

                        it.add(
                            Attachment(
                                dayId = null,
                                content = bytes,
                                mimeType = mimeType,
                                id = null
                            )
                        )
                    }
                }

            _tempDay.value = currentDay.copy(attachments = newAttachments)
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
