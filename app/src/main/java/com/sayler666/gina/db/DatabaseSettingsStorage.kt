package com.sayler666.gina.db

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sayler666.gina.db.DatabaseSettingsStorageImpl.PreferencesKeys.DATABASE_PATH
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface DatabaseSettingsStorage {
    fun getDatabasePathFlow(): Flow<String?>
    suspend fun saveDatabasePath(path: String)
    suspend fun clearDatabasePath()
}

class DatabaseSettingsStorageImpl @Inject constructor(private val app: Application) :
    DatabaseSettingsStorage {
    private val Context.dataStore by preferencesDataStore(
        name = PREFERENCES_NAME
    )

    override fun getDatabasePathFlow(): Flow<String?> = app.dataStore.data.map { pref ->
        pref[DATABASE_PATH]
    }

    override suspend fun saveDatabasePath(path: String) {
        app.dataStore.edit { preferences ->
            preferences[DATABASE_PATH] = path
        }
    }

    override suspend fun clearDatabasePath() {
        app.dataStore.edit { preferences ->
            preferences.minusAssign(DATABASE_PATH)
        }
    }

    private object PreferencesKeys {
        val DATABASE_PATH = stringPreferencesKey("DATABASE_PATH")
    }

    companion object {
        const val PREFERENCES_NAME = "DATABASE_PREFERENCES"
    }
}
