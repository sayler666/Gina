package com.sayler.data.settings


interface ISettingsRepository {

    fun get(): SettingsState
    fun save(settingsState: SettingsData)
    fun clear()
}
