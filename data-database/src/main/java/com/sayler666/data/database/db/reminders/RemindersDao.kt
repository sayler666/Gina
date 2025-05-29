package com.sayler666.data.database.db.reminders

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RemindersDao {
    @Query("SELECT * FROM reminders")
    fun getReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders ORDER BY id DESC LIMIT 1 ")
    fun getLastReminderFlow(): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders ORDER BY id")
    suspend fun getAllReminders(): List<ReminderEntity>

    @Insert
    suspend fun addReminder(reminder: ReminderEntity): Long

    @Delete
    suspend fun deleteReminders(reminder: ReminderEntity): Int

    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders(): Int
}
