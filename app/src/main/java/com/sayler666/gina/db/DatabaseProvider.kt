package com.sayler666.gina.db

import android.app.Application
import androidx.room.Room
import com.sayler666.gina.settings.Settings
import timber.log.Timber

class DatabaseProvider(private val application: Application, private val settings: Settings) {
    private var db: GinaDatabase? = null

    fun openSavedDB(): Boolean {
        val savedPath = settings.getDatabasePath()
        savedPath?.let {
            try {
                db = Room.databaseBuilder(application, GinaDatabase::class.java, savedPath).build()
            } catch (e: Exception) {
                Timber.e(e, "Error opening DB")
                return false
            }
            return true
        }
        return false
    }

    fun openDB(path: String): Boolean {
        try {
            db = Room.databaseBuilder(application, GinaDatabase::class.java, path).build()
        } catch (e: Exception) {
            Timber.e(e, "Error opening DB")
            return false
        }
        // update settings
        settings.saveDatabasePath(path)
        return true
    }

    fun getOpenedDb(): GinaDatabase? {
        return db
    }
}
