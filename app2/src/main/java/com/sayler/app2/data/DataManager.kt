package com.sayler.app2.data

import android.content.Context
import android.os.Debug
import androidx.room.Room
import com.sayler.data.days.GinaRoomDatabase
import com.sayler.data.settings.SettingsData
import com.sayler.data.settings.SettingsRepository
import com.sayler.data.settings.SettingsState

class DataManager constructor(
        private val settingsRepository: SettingsRepository,
        private val context: Context
) {
    lateinit var ginaRoomDatabase: GinaRoomDatabase

    init {
        val settings = settingsRepository.get()
        val databasePath = when (settings) {
            SettingsState.NotSet -> "days.db"
            is SettingsState.Set -> settings.settingsData.databasePath
        }
        openDb(databasePath)
    }

    fun setSourceFile(databasePath: String) {
        settingsRepository.save(SettingsData(databasePath))
        openDb(databasePath)
    }

    fun isDbOpen() = when (settingsRepository.get()) {
        SettingsState.NotSet -> false
        is SettingsState.Set -> true
    }

    fun <T> dao(dao: GinaRoomDatabase.() -> T): T {
        return ginaRoomDatabase.dao()
    }

    fun close() {
        ginaRoomDatabase.close()
    }

    private fun openDb(databasePath: String) {
        val builder = Room.databaseBuilder(context, GinaRoomDatabase::class.java, databasePath)
                .fallbackToDestructiveMigration()
        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }

        ginaRoomDatabase = builder.build()
    }
}

