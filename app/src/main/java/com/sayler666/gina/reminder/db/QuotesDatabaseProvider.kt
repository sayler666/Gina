package com.sayler666.gina.reminder.db

import android.app.Application
import androidx.room.Room

import timber.log.Timber

class RemindersDatabaseProvider(
    private val application: Application
) {
    private var INSTANCE: ReminderDatabase? = null

    fun getDB(): ReminderDatabase? {
        try {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(application, ReminderDatabase::class.java, "Reminders")
                            .build()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error opening DB")
            return null
        }
        return INSTANCE
    }
}

suspend fun RemindersDatabaseProvider.withRemindersDao(action: suspend RemindersDao.() -> Unit) {
    getDB()?.reminderDao()?.action()
}

suspend fun <T> RemindersDatabaseProvider.returnWithRemindersDao(action: suspend RemindersDao.() -> T): T? {
    return getDB()?.reminderDao()?.action()
}
