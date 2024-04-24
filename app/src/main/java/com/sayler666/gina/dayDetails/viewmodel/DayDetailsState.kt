package com.sayler666.gina.dayDetails.viewmodel

import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.friends.ui.FriendState
import com.sayler666.gina.mood.Mood

data class DayDetailsState(
    val id: Int,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val content: String,
    val mood: Mood = Mood.EMPTY,
    val attachments: List<AttachmentState> = emptyList(),
    val friends: List<FriendState> = emptyList()
)
