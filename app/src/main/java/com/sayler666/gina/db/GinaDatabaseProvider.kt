package com.sayler666.gina.db

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import kotlinx.coroutines.flow.first
import timber.log.Timber

class GinaDatabaseProvider(
    private val application: Application,
    private val databaseSettings: DatabaseSettings
) {
    private var databaseInstance: GinaDatabase? = null

    suspend fun openSavedDB(): Boolean {
        val savedPath = databaseSettings.getDatabasePathFlow().first()
        savedPath?.let {
            try {
                if (databaseInstance == null) {
                    synchronized(this) {
                        databaseInstance =
                            Room.databaseBuilder(application, GinaDatabase::class.java, savedPath)
                                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                                .build()
                        Timber.d("GinaDatabaseProvider: Database instance created")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "GinaDatabaseProvider: Error opening DB")
                return false
            }
            return true
        }
        return false
    }

    suspend fun openAndRememberDB(path: String): Boolean {
        try {
            if (databaseInstance == null) {
                synchronized(this) {
                    databaseInstance =
                        Room.databaseBuilder(application, GinaDatabase::class.java, path)
                            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                            .build()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "GinaDatabaseProvider: Error opening DB")
            return false
        }
        // update settings
        databaseSettings.saveDatabasePath(path)
        return true
    }

    fun getOpenedDb(): GinaDatabase? = databaseInstance
}

suspend fun GinaDatabaseProvider.withDaysDao(action: suspend DaysDao.() -> Unit) {
    getOpenedDb()?.daysDao()?.action()
}

suspend fun GinaDatabaseProvider.transactionWithDaysDao(action: suspend DaysDao.() -> Unit) {
    getOpenedDb()?.withTransaction {
        getOpenedDb()?.daysDao()?.action()
    }
}

suspend fun <T> GinaDatabaseProvider.returnWithDaysDao(action: suspend DaysDao.() -> T): T? {
    return getOpenedDb()?.daysDao()?.action()
}
