package com.sayler666.gina.addDay.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.addDay.ui.AddDayScreenNavArgs
import com.sayler666.gina.addDay.usecase.AddDayUseCase
import com.sayler666.gina.core.flow.Event
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsMapper
import com.sayler666.gina.dayDetails.viewmodel.DayWithAttachmentsEntity
import com.sayler666.gina.daysList.viewmodel.Mood
import com.sayler666.gina.db.Attachment
import com.sayler666.gina.db.Day
import com.sayler666.gina.db.DayWithAttachment
import com.sayler666.gina.destinations.AddDayScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class AddDayViewModel @Inject constructor(
    private val dayDetailsMapper: DayDetailsMapper,
    private val addDayUseCase: AddDayUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navArgs: AddDayScreenNavArgs =
        AddDayScreenDestination.argsFrom(savedStateHandle)
    private val date: LocalDate?
        get() = navArgs.date

    private val _tempDay: MutableStateFlow<DayWithAttachment?> = MutableStateFlow(
        DayWithAttachment(
            Day(
                id = null,
                date = (date?.atStartOfDay()?.toEpochSecond(ZoneOffset.UTC)
                    ?: (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))) * 1000,
                content = "",
                mood = 0
            ), emptyList()
        )
    )
    val tempDay: StateFlow<DayWithAttachmentsEntity?>
        get() = _tempDay
            .filterNotNull()
            .map(dayDetailsMapper::mapToVm)
            .stateIn(
                viewModelScope,
                WhileSubscribed(500),
                null
            )

    private val _navigateBack: MutableStateFlow<Event<Unit>> = MutableStateFlow(Event.Empty)
    val navigateBack: StateFlow<Event<Unit>>
        get() = _navigateBack

    fun setNewContent(newContent: String) {
        val temp = _tempDay.value ?: return
        _tempDay.value = temp.copy(day = temp.day.copy(content = newContent))
    }

    fun setNewDate(epochMilliseconds: Long) {
        val currentDay = _tempDay.value ?: return
        _tempDay.value = currentDay.copy(day = currentDay.day.copy(date = epochMilliseconds))
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

    fun addAttachment(content: ByteArray, mimeType: String) {
        val currentDay = _tempDay.value ?: return
        val newAttachments = currentDay.attachments
            .toMutableList()
            .also {
                it.add(
                    Attachment(
                        dayId = null,
                        content = content,
                        mimeType = mimeType,
                        id = null
                    )
                )
            }

        _tempDay.value = currentDay.copy(attachments = newAttachments)
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
