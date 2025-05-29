package com.sayler666.data.database.db.journal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sayler666.domain.model.journal.Day
import com.sayler666.domain.model.journal.Mood
import java.time.LocalDate

@Entity(tableName = "days")
data class DayEntity(
    @ColumnInfo(name = "date", typeAffinity = ColumnInfo.INTEGER)
    val date: LocalDate?,

    @ColumnInfo(name = "content", typeAffinity = ColumnInfo.TEXT)
    val content: String?,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int?,

    @ColumnInfo(name = "mood", typeAffinity = ColumnInfo.INTEGER)
    val mood: Mood?,
) {
    companion object {
        fun DayEntity.toModel() = Day(
            date = date ?: LocalDate.now(),
            content = content.orEmpty(),
            id = id ?: -1,
            mood = mood ?: Mood.EMPTY,
        )

        fun Day.toEntity() = DayEntity(
            date = date,
            content = content,
            id = if(id == -1) null else id,
            mood = mood,
        )
    }
}

