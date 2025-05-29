package com.sayler666.data.database.db.journal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sayler666.domain.model.journal.Friend
import com.sayler666.domain.model.journal.FriendWithCount

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "friend_id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "avatar", typeAffinity = ColumnInfo.BLOB)
    val avatar: ByteArray?
) {
    companion object {
        fun FriendEntity.toModel() = Friend(
            id = id,
            name = name,
            avatar = avatar
        )

        fun Friend.toEntity() = FriendEntity(
            id = id,
            name = name,
            avatar = avatar
        )
    }
}
