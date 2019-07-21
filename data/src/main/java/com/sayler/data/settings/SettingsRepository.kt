package com.sayler.data.settings

import android.content.SharedPreferences
import com.squareup.moshi.Moshi

class SettingsRepository constructor(
        private val sharedPreferences: SharedPreferences,
        private val moshi: Moshi
) : ISettingsRepository {

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
