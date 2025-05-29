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
import com.sayler666.gina.friends.viewmodel.FriendEntity
import com.sayler666.gina.friends.viewmodel.FriendsMapper
import java.time.LocalDate
import javax.inject.Inject

@Deprecated("Use state class for each view")
class DayDetailsMapper @Inject constructor(
    private val friendsMapper: FriendsMapper,
) {
    fun mapToVm(
        day: DayDetails,
        allFriends: List<FriendWithCount> = emptyList(),
        friendsSearchQuery: String? = null
    ): DayDetailsEntity {
        requireNotNull(day.day.date)
        requireNotNull(day.day.content)
        return DayDetailsEntity(
            id = day.day.id,
            dayOfMonth = getDayOfMonth(day.day.date),
            dayOfWeek = getDayOfWeek(day.day.date),
            yearAndMonth = getYearAndMonth(day.day.date),
            localDate = day.day.date,
            content = day.day.content,
            attachments = day.attachments.map(Attachment::toState),
            mood = day.day.mood,
            friendsAll = friendsMapper.mapToDayFriends(day.friends, allFriends, friendsSearchQuery)
        )
    }
}

@Deprecated("Use state class for each view")
data class DayDetailsEntity(
    val id: Int?,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val localDate: LocalDate,
    val content: String,
    val mood: Mood?,
    val attachments: List<AttachmentState> = emptyList(),
    val friendsAll: List<FriendEntity> = emptyList()
) {
    val friendsSelected: List<FriendEntity>
        get() = friendsAll.filter { it.selected }
}
