package com.sayler666.gina.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.sayler666.gina.mood.Mood
import java.time.LocalDate

@Entity(tableName = "days")
data class Day(
    @ColumnInfo(name = "date", typeAffinity = ColumnInfo.INTEGER)
    val date: LocalDate?,

    @ColumnInfo(name = "content", typeAffinity = ColumnInfo.TEXT)
    val content: String?,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int?,

    @ColumnInfo(name = "mood", typeAffinity = ColumnInfo.INTEGER)
    val mood: Mood?,
)

data class AttachmentWithDay(
    @Embedded val attachment: Attachment,
    @Embedded val day: Day
)

data class DayDetails(
    @Embedded val day: Day,
    @Relation(
        parentColumn = "id",
        entityColumn = "days_id"
    )
    val attachments: List<Attachment>,
    @Relation(
        parentColumn = "id",
        entityColumn = "friend_id",
        associateBy = Junction(DayFriends::class)
    )
    val friends: List<Friend>
)
