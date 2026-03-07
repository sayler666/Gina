package com.sayler666.gina.dayDetails.viewmodel

import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.domain.model.journal.DayDetails
import com.sayler666.domain.model.journal.FriendWithCount
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.attachments.viewmodel.toState
import com.sayler666.gina.friends.ui.FriendState
import com.sayler666.gina.friends.viewmodel.FriendsMapper
import java.time.LocalDate

data class DayDetailsEntity(
    val id: Int?,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val localDate: LocalDate,
    val content: String,
    val mood: Mood?,
    val attachments: List<AttachmentState> = emptyList(),
    val friendsAll: List<FriendState> = emptyList()
) {
    val friendsSelected: List<FriendState>
        get() = friendsAll.filter { it.selected }
}

fun DayDetails.toEditState(
    friendsMapper: FriendsMapper,
    allFriends: List<FriendWithCount> = emptyList(),
    friendsSearchQuery: String? = null
): DayDetailsEntity {
    requireNotNull(day.date)
    requireNotNull(day.content)
    return DayDetailsEntity(
        id = day.id,
        dayOfMonth = getDayOfMonth(day.date),
        dayOfWeek = getDayOfWeek(day.date),
        yearAndMonth = getYearAndMonth(day.date),
        localDate = day.date,
        content = day.content,
        attachments = attachments.map(Attachment::toState),
        mood = day.mood,
        friendsAll = friendsMapper.mapToDayFriends(friends, allFriends, friendsSearchQuery)
    )
}
