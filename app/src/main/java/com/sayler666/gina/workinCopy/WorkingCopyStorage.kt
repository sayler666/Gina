package com.sayler666.gina.workinCopy

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sayler666.core.string.getTextWithoutHtml
import com.sayler666.gina.workinCopy.WorkingCopyStorageImpl.PreferencesKeys.TEXT_CONTENT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface WorkingCopyStorage {
    fun getTextContent(): Flow<String?>
    suspend fun store(textContent: String)
    suspend fun clear()
}

@Singleton
class WorkingCopyStorageImpl @Inject constructor(private val app: Application) :
    WorkingCopyStorage {
    private val Context.dataStore by preferencesDataStore(
        name = PREFERENCES_NAME
    )

    override fun getTextContent(): Flow<String?> = app.dataStore.data.map { pref ->
        pref[TEXT_CONTENT]
    }

    override suspend fun store(textContent: String) {
        // Store only if not blank (excluding html)
        if (textContent.getTextWithoutHtml().isNotBlank()) {
            app.dataStore.edit { preferences ->
                preferences[TEXT_CONTENT] = textContent
            }
        }
    }

    override suspend fun clear() {
        app.dataStore.edit { preferences ->
            preferences.minusAssign(TEXT_CONTENT)
        }
    }

    private object PreferencesKeys {
        val TEXT_CONTENT = stringPreferencesKey("TEXT_CONTENT")
    }

    companion object {
        const val PREFERENCES_NAME = "WORKING_COPY"
    }
}
