package com.sayler666.gina.db

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.first
import timber.log.Timber

class DatabaseProvider(private val application: Application, private val databaseSettings: DatabaseSettings) {
    private var db: GinaDatabase? = null

    suspend fun openSavedDB(): Boolean {
        val savedPath = databaseSettings.getDatabasePathFlow().first()
        savedPath?.let {
            try {
                db = Room.databaseBuilder(application, GinaDatabase::class.java, savedPath)
                    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                    .build()
            } catch (e: Exception) {
                Timber.e(e, "Error opening DB")
                return false
            }
            return true
        }
        return false
    }

    suspend fun openDB(path: String): Boolean {
        try {
            db = Room.databaseBuilder(application, GinaDatabase::class.java, path)
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                .build()
        } catch (e: Exception) {
            Timber.e(e, "Error opening DB")
            return false
        }
        // update settings
        databaseSettings.saveDatabasePath(path)
        return true
    }

    fun getOpenedDb(): GinaDatabase? = db
}

suspend fun DatabaseProvider.withDaysDao(action: suspend DaysDao.() -> Unit) {
    getOpenedDb()?.daysDao()?.action()
}
