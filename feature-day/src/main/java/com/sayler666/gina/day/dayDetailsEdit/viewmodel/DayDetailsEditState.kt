package com.sayler666.gina.day.dayDetailsEdit.viewmodel

import androidx.compose.runtime.Stable
import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.domain.model.journal.DayDetails
import com.sayler666.domain.model.journal.Friend
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.day.attachments.viewmodel.toState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDate

@Stable
data class DayDetailsEditState(
    val id: Int?,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val localDate: LocalDate,
    val content: String,
    val mood: Mood?,
    val attachments: ImmutableList<AttachmentState> = persistentListOf(),
    val changesExist: Boolean = false,
    val hasWorkingCopy: Boolean = false,
    val friends: ImmutableList<Friend> = persistentListOf(),
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
        attachments = attachments.map(Attachment::toState).toImmutableList(),
        mood = day.mood,
        friends = friends.toImmutableList(),
    )
}
