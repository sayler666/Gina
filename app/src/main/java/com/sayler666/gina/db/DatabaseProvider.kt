package com.sayler666.gina.db

import android.app.Application
import androidx.room.Room
import com.sayler666.gina.settings.Settings

class DatabaseProvider(private val application: Application, private val settings: Settings) {
    private var db: GinaDatabase? = null

    fun openSavedDB(): DatabaseProvider {
        val savedPath = settings.getDatabasePath()
        savedPath?.let {
            db = Room.databaseBuilder(application, GinaDatabase::class.java, savedPath).build()
        }
        return this
    }

    fun openDB(path: String): DatabaseProvider {
        db = Room.databaseBuilder(application, GinaDatabase::class.java, path).build()
        // update settings
        settings.saveDatabasePath(path)
        return this
    }

    fun getOpenedDb(): GinaDatabase? {
        return db
    }
}
