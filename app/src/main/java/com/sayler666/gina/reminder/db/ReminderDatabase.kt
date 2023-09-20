package com.sayler666.gina.reminder.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sayler666.gina.reminder.db.converter.LocalTimeConverter
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

@Database(
    entities = [Reminder::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalTimeConverter::class)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): RemindersDao
}

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "time")
    val time: LocalTime
)

@Dao
interface RemindersDao {
    @Query("SELECT * FROM reminders")
    fun getReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders ORDER BY id DESC LIMIT 1 ")
    fun getLastReminderFlow(): Flow<Reminder?>

    @Query("SELECT * FROM reminders ORDER BY id")
    suspend fun getAllReminders(): List<Reminder>

    @Insert
    suspend fun addReminder(reminder: Reminder): Long

    @Delete
    suspend fun deleteReminders(reminder: Reminder): Int

    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders(): Int
}
