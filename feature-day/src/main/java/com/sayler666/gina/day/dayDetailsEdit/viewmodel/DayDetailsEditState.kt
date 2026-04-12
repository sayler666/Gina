package com.sayler666.gina.day.dayDetailsEdit.viewmodel

import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.domain.model.journal.DayDetails
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.day.attachments.viewmodel.toState
import java.time.LocalDate

data class DayDetailsEditState(
    val id: Int?,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val localDate: LocalDate,
    val content: String,
    val mood: Mood?,
    val attachments: List<AttachmentState> = emptyList(),
    val changesExist: Boolean = false,
    val hasWorkingCopy: Boolean = false,
)

fun DayDetails.toEditState(): DayDetailsEditState {
    requireNotNull(day.date)
    requireNotNull(day.content)
    return DayDetailsEditState(
        id = day.id,
        dayOfMonth = getDayOfMonth(day.date),
        dayOfWeek = getDayOfWeek(day.date),
        yearAndMonth = getYearAndMonth(day.date),
        localDate = day.date,
        content = day.content,
        attachments = attachments.map(Attachment::toState),
        mood = day.mood,
    )
}
