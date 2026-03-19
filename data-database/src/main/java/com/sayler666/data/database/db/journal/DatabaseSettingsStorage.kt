package com.sayler666.data.database.db.journal

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sayler666.data.database.db.journal.DatabaseSettingsStorageImpl.PreferencesKeys.DATABASE_PATH
import com.sayler666.data.database.db.journal.DatabaseSettingsStorageImpl.PreferencesKeys.EXTERNAL_DB_URI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface DatabaseSettingsStorage {
    fun getDatabasePathFlow(): Flow<String?>
    suspend fun saveDatabasePath(path: String)
    fun getExternalDbUriFlow(): Flow<String?>
    suspend fun saveExternalDbUri(uri: String)
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

    override fun getExternalDbUriFlow(): Flow<String?> = app.dataStore.data.map { pref ->
        pref[EXTERNAL_DB_URI]
    }

    override suspend fun saveExternalDbUri(uri: String) {
        app.dataStore.edit { preferences ->
            preferences[EXTERNAL_DB_URI] = uri
        }
    }

    private object PreferencesKeys {
        val DATABASE_PATH = stringPreferencesKey("DATABASE_PATH")
        val EXTERNAL_DB_URI = stringPreferencesKey("EXTERNAL_DB_URI")
    }

    companion object {
        const val PREFERENCES_NAME = "DATABASE_PREFERENCES"
    }
}
