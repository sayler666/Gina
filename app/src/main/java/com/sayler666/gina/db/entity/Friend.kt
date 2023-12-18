package com.sayler666.gina.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "friend_id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "avatar", typeAffinity = ColumnInfo.BLOB)
    val avatar: ByteArray?
)

@Entity(
    tableName = "daysFriends",
    primaryKeys = ["id", "friend_id"],
    foreignKeys = [
        ForeignKey(
            entity = Day::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Friend::class,
            parentColumns = ["friend_id"],
            childColumns = ["friend_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DayFriends(
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "friend_id")
    val friendId: Int
)

data class FriendWithCount(
    val friendId: Int,
    val friendName: String,
    val friendAvatar: ByteArray?,
    val daysCount: Int
)
