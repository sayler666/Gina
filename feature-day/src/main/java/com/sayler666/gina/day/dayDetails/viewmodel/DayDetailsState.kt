package com.sayler666.gina.day.dayDetails.viewmodel

import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.core.string.getTextWithoutHtml
import com.sayler666.core.string.scrambleText
import com.sayler666.domain.model.journal.DayDetails
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.day.attachments.viewmodel.toState
import com.sayler666.gina.friends.ui.FriendState
import com.sayler666.gina.friends.viewmodel.createInitials

data class DayDetailsState(
    val id: Int,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val content: String,
    val mood: Mood = Mood.EMPTY,
    val attachments: List<AttachmentState> = emptyList(),
    val friends: List<FriendState> = emptyList(),
)

fun DayDetails.toState(incognitoMode: Boolean = false) = DayDetailsState(
    id = this.day.id,
    dayOfMonth = getDayOfMonth(day.date),
    dayOfWeek = getDayOfWeek(day.date),
    yearAndMonth = getYearAndMonth(day.date),
    content = day.content.let { if (incognitoMode) it.getTextWithoutHtml().scrambleText() else it },
    mood = day.mood,
    attachments = attachments
        .filterNot { it.hidden && incognitoMode }
        .map { it.toState() },
    friends = friends.map {
        FriendState(id = it.id, name = it.name, avatar = it.avatar, initials = createInitials(it.name))
    },
)
