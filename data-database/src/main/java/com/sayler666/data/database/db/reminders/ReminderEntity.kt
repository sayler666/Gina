package com.sayler666.data.database.db.reminders

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sayler666.domain.model.reminders.Reminder
import java.time.LocalTime

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "time")
    val time: LocalTime
) {
    companion object {
        fun ReminderEntity.toModel() = Reminder(
            id = id,
            time = time,
        )

        fun Reminder.toModel() = ReminderEntity(
            id = id,
            time = time,
        )
    }
}
