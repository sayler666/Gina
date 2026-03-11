package com.sayler666.gina.day.addDay.ui

import com.sayler666.data.database.db.quotes.QuoteEntity
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.friends.ui.FriendState
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
    val friendsAll: List<FriendState> = emptyList(),
    val quote: QuoteEntity?,
    val workingCopyExists: Boolean
) {
    val friendsSelected: List<FriendState>
        get() = friendsAll.filter { it.selected }
}
