package com.sayler.data.settings

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi


sealed class SettingsState {
    object NotSet : SettingsState()
    data class Set(val settingsData: SettingsData) : SettingsState()
}

fun SettingsData.toJson(moshi: Moshi) = moshi.adapter(SettingsData::class.java).toJson(this)
fun String.fromJson(moshi: Moshi) = moshi.adapter(SettingsData::class.java).fromJson(this)

@JsonClass(generateAdapter = true)
data class SettingsData(
        val databasePath: String
)
