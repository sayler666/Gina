package com.sayler666.gina.feature.settings

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sayler666.gina.feature.settings.AppSettingsImpl.PreferencesKeys.INCOGNITO_MODE
import com.sayler666.gina.feature.settings.AppSettingsImpl.PreferencesKeys.THEME
import com.sayler666.gina.ui.theme.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AppSettings {
    fun getThemeFlow(): Flow<Theme>
    suspend fun saveTheme(theme: Theme)
    fun getIncognitoModeFlow(): Flow<Boolean>
    suspend fun saveIncognitoMode(enabled: Boolean)
}

class AppSettingsImpl @Inject constructor(private val app: Application) : AppSettings {
    private val Context.dataStore by preferencesDataStore(
        name = PREFERENCES_NAME,
    )

    override fun getThemeFlow(): Flow<Theme> = app.dataStore.data.map { pref ->
        Theme.fromKey(pref[THEME] ?: Theme.default().key)
    }

    override suspend fun saveTheme(theme: Theme) {
        app.dataStore.edit { preferences ->
            preferences[THEME] = theme.key
        }
    }

    override fun getIncognitoModeFlow(): Flow<Boolean> = app.dataStore.data.map { pref ->
        pref[INCOGNITO_MODE] ?: false
    }

    override suspend fun saveIncognitoMode(enabled: Boolean) {
        app.dataStore.edit { preferences ->
            preferences[INCOGNITO_MODE] = enabled
        }
    }

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("THEME")
        val INCOGNITO_MODE = booleanPreferencesKey("INCOGNITO_MODE")
    }

    companion object {
        const val PREFERENCES_NAME = "APP_PREFERENCES"
    }
}
