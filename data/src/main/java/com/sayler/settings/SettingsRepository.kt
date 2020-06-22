package com.sayler.settings

import android.content.SharedPreferences
import com.squareup.moshi.Moshi

interface SettingsRepository {

    fun get(): SettingsState
    fun save(settingsState: SettingsData)
    fun clear()
}

class SettingsRepositoryImpl constructor(
    private val sharedPreferences: SharedPreferences,
    private val moshi: Moshi
) : SettingsRepository {

    override fun get(): SettingsState = sharedPreferences.getString(KEY, null)
        ?.fromJson(moshi)
        ?.let {
            SettingsState.Set(it)
        } ?: SettingsState.NotSet


    override fun save(settingsState: SettingsData) {
        sharedPreferences.edit()
            .putString(KEY, settingsState.toJson(moshi))
            .apply()
    }

    override fun clear() {
        sharedPreferences.edit()
            .remove(KEY)
            .apply()
    }

    companion object {
        private const val KEY = "SETTINGS_KEY"
    }
}