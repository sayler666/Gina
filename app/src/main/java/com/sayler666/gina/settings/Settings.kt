package com.sayler666.gina.settings

import android.app.Application
import android.content.Context.MODE_PRIVATE
import javax.inject.Inject

interface Settings {
    fun getDatabasePath(): String?
    fun saveDatabasePath(path: String)
    fun clearDatabasePath()
}

class SettingsImpl @Inject constructor(private val app: Application) : Settings {
    override fun getDatabasePath(): String? =
        with(app.getSharedPreferences(SP_NAME, MODE_PRIVATE)) {
            return@with getString(DATABASE_PATH_KEY, null)
        }

    override fun saveDatabasePath(path: String) {
        with(app.getSharedPreferences(SP_NAME, MODE_PRIVATE).edit()) {
            putString(DATABASE_PATH_KEY, path).apply()
        }
    }

    override fun clearDatabasePath() {
        with(app.getSharedPreferences(SP_NAME, MODE_PRIVATE).edit()) {
            remove(DATABASE_PATH_KEY)
        }
    }

    companion object {
        private const val SP_NAME = "SP_SETTINGS"
        private const val DATABASE_PATH_KEY = "DATABASE_PATH"
    }
}
