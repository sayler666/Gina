package com.sayler666.gina.settings

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sayler666.gina.settings.AppSettingsImpl.PreferencesKeys.THEME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AppSettings {
    fun getThemeFlow(): Flow<Theme>
    suspend fun saveTheme(theme: Theme)
}

class AppSettingsImpl @Inject constructor(private val app: Application) : AppSettings {
    private val Context.dataStore by preferencesDataStore(
        name = PREFERENCES_NAME,
    )

    override fun getThemeFlow(): Flow<Theme> = app.dataStore.data.map { pref ->
        Theme.valueOf(pref[THEME] ?: Theme.default().name)
    }

    override suspend fun saveTheme(theme: Theme) {
        app.dataStore.edit { preferences ->
            preferences[THEME] = theme.name
        }
    }


    private object PreferencesKeys {
        val THEME = stringPreferencesKey("THEME")
    }

    companion object {
        const val PREFERENCES_NAME = "APP_PREFERENCES"
    }
}

enum class Theme {
    AutoDarkLight,
    Dynamic,
    Dark,
    Light;

    companion object {
        fun default(): Theme = Dynamic
    }
}
