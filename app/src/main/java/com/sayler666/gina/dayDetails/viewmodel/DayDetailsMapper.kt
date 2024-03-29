package com.sayler666.gina.dayDetails.viewmodel

import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity
import com.sayler666.gina.attachments.viewmodel.AttachmentMapper
import com.sayler666.gina.db.entity.Attachment
import com.sayler666.gina.db.entity.DayDetails
import com.sayler666.gina.db.entity.FriendWithCount
import com.sayler666.gina.friends.viewmodel.FriendEntity
import com.sayler666.gina.friends.viewmodel.FriendsMapper
import com.sayler666.gina.mood.Mood
import java.time.LocalDate
import javax.inject.Inject


class DayDetailsMapper @Inject constructor(
    private val friendsMapper: FriendsMapper,
    private val attachmentMapper: AttachmentMapper
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
            attachments = mapAttachments(day.attachments),
            mood = day.day.mood,
            friendsAll = friendsMapper.mapToDayFriends(day.friends, allFriends, friendsSearchQuery)
        )
    }

    private fun mapAttachments(attachments: List<Attachment>): List<AttachmentEntity> =
        attachments.map(attachmentMapper::mapToAttachmentEntity)
}

data class DayDetailsEntity(
    val id: Int?,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val localDate: LocalDate,
    val content: String,
    val mood: Mood?,
    val attachments: List<AttachmentEntity> = emptyList(),
    val friendsAll: List<FriendEntity> = emptyList()
) {
    val friendsSelected: List<FriendEntity>
        get() = friendsAll.filter { it.selected }
}

