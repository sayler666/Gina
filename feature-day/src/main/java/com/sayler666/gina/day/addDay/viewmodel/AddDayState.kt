package com.sayler666.gina.day.addDay.viewmodel

import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.domain.model.journal.DayDetails
import com.sayler666.domain.model.journal.Friend
import com.sayler666.domain.model.journal.Mood
import com.sayler666.domain.model.quotes.Quote
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.day.attachments.viewmodel.toState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDate

data class AddDayState(
    val id: Int?,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val localDate: LocalDate,
    val content: String,
    val mood: Mood = Mood.EMPTY,
    val attachments: List<AttachmentState> = emptyList(),
    val quote: Quote?,
    val workingCopyExists: Boolean,
    val friends: ImmutableList<Friend> = persistentListOf(),
)

fun DayDetails.toAddDayState(quote: Quote?, hasWorkingCopy: Boolean) =  AddDayState(
    id = day.id,
    dayOfMonth = getDayOfMonth(day.date),
    dayOfWeek = getDayOfWeek(day.date),
    yearAndMonth = getYearAndMonth(day.date),
    localDate = day.date,
    content = day.content,
    attachments = attachments.map(Attachment::toState),
    mood = day.mood,
    quote = quote,
    workingCopyExists = hasWorkingCopy,
    friends = friends.toImmutableList(),
)
