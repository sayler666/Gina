package com.sayler666.gina.dayDetails.viewmodel

import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.attachments.viewmodel.toState
import com.sayler666.gina.db.entity.Attachment
import com.sayler666.gina.db.entity.DayDetails
import com.sayler666.gina.db.entity.FriendWithCount
import com.sayler666.gina.friends.ui.FriendState
import com.sayler666.gina.friends.viewmodel.FriendEntity
import com.sayler666.gina.friends.viewmodel.FriendsMapper
import com.sayler666.gina.friends.viewmodel.createInitials
import com.sayler666.gina.mood.Mood
import java.time.LocalDate
import javax.inject.Inject

fun DayDetails.toState() = DayDetailsState(
    id = this.day.id!!,
    dayOfMonth = getDayOfMonth(day.date!!),
    dayOfWeek = getDayOfWeek(day.date),
    yearAndMonth = getYearAndMonth(day.date),
    content = day.content!!,
    mood = day.mood!!,
    attachments = attachments.map(Attachment::toState),
    friends = friends.map {
        FriendState(name = it.name, avatar = it.avatar, initials = createInitials(it.name))
    }
)

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
