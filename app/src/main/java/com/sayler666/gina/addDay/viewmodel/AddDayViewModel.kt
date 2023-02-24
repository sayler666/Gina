package com.sayler666.gina.addDay.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.addDay.ui.AddDayScreenNavArgs
import com.sayler666.gina.addDay.usecase.AddDayUseCase
import com.sayler666.gina.core.date.toEpochMilliseconds
import com.sayler666.gina.core.file.isImageMimeType
import com.sayler666.gina.core.flow.Event
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsMapper
import com.sayler666.gina.dayDetails.viewmodel.DayWithAttachmentsEntity
import com.sayler666.gina.db.Attachment
import com.sayler666.gina.db.Day
import com.sayler666.gina.db.DayWithAttachment
import com.sayler666.gina.destinations.AddDayScreenDestination
import com.sayler666.gina.imageCompressor.ImageCompressor
import com.sayler666.gina.ui.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddDayViewModel @Inject constructor(
    private val dayDetailsMapper: DayDetailsMapper,
    private val addDayUseCase: AddDayUseCase,
    savedStateHandle: SavedStateHandle,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }
    private val navArgs: AddDayScreenNavArgs =
        AddDayScreenDestination.argsFrom(savedStateHandle)
    private val date: LocalDate?
        get() = navArgs.date

    private val blankDay = DayWithAttachment(
        Day(
            id = null,
            date = (date?.atStartOfDay()?.toLocalDate() ?: LocalDate.now()).toEpochMilliseconds(),
            content = "",
            mood = 0
        ), emptyList(), emptyList()
    )

    private val _tempDay: MutableStateFlow<DayWithAttachment?> = MutableStateFlow(blankDay)
    val tempDay: StateFlow<DayWithAttachmentsEntity?>
        get() = _tempDay
            .filterNotNull()
            .map(dayDetailsMapper::mapToVm)
            .stateIn(
                viewModelScope,
                WhileSubscribed(500),
                null
            )

    val changesExist: StateFlow<Boolean> = _tempDay.flatMapLatest {
        flow {
            if (it != null) emit(it != blankDay)
        }
    }.stateIn(
        viewModelScope,
        WhileSubscribed(500),
        false
    )

    private val _navigateBack: MutableStateFlow<Event<Unit>> = MutableStateFlow(Event.Empty)
    val navigateBack: StateFlow<Event<Unit>>
        get() = _navigateBack

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
                attachments.removeIf { it.content.hashCode() == byteHashCode }
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

    fun saveChanges() {
        _tempDay.value?.let {
            viewModelScope.launch {
                addDayUseCase.addDay(it)
                _navigateBack.tryEmit(Event.Value(Unit))
            }
        }
    }
}
