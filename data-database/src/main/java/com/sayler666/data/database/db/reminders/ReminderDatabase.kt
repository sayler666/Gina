package com.sayler666.data.database.db.reminders

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sayler666.data.database.db.reminders.converter.LocalTimeConverter

@Database(
    entities = [ReminderEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalTimeConverter::class)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): RemindersDao
}
