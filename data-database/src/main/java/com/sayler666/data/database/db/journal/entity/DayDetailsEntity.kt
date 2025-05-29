package com.sayler666.data.database.db.journal.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sayler666.data.database.db.journal.entity.AttachmentEntity.Companion.toModel
import com.sayler666.data.database.db.journal.entity.DayEntity.Companion.toModel
import com.sayler666.data.database.db.journal.entity.FriendEntity.Companion.toModel
import com.sayler666.domain.model.journal.DayDetails

data class DayDetailsEntity(
    @Embedded val day: DayEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "days_id"
    )
    val attachments: List<AttachmentEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "friend_id",
        associateBy = Junction(DayFriendsEntity::class)
    )
    val friends: List<FriendEntity>
) {
    companion object {
        fun DayDetailsEntity.toModel() = DayDetails(
            day = day.toModel(),
            attachments = attachments.map { it.toModel() },
            friends = friends.map { it.toModel() }
        )
    }
}
