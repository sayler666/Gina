package com.sayler666.data.database.db.journal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "daysFriends",
    primaryKeys = ["id", "friend_id"],
    foreignKeys = [
        ForeignKey(
            entity = DayEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FriendEntity::class,
            parentColumns = ["friend_id"],
            childColumns = ["friend_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DayFriendsEntity(
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "friend_id")
    val friendId: Int
)
