package com.sayler.app2.data

import android.content.Context
import android.os.Debug
import androidx.room.Room
import com.sayler.data.days.GinaRoomDatabase
import com.sayler.data.settings.SettingsData
import com.sayler.data.settings.SettingsRepository
import com.sayler.data.settings.SettingsState
import javax.inject.Inject

class DataManager @Inject constructor(
        private val settingsRepository: SettingsRepository,
        private val context: Context
) : IDataManager {

    lateinit var ginaRoomDatabase: GinaRoomDatabase

    init {
        val settings = settingsRepository.get()
        val databasePath = when (settings) {
            SettingsState.NotSet -> "days.db"
            is SettingsState.Set -> settings.settingsData.databasePath
        }
        openDb(databasePath)
    }

    override fun setSourceFile(databasePath: String) {
        settingsRepository.save(SettingsData(databasePath))
        openDb(databasePath)
    }

    override fun isDbOpen() = when (settingsRepository.get()) {
        SettingsState.NotSet -> false
        is SettingsState.Set -> true
    }

    override fun <T> dao(block: GinaRoomDatabase.() -> T): T = ginaRoomDatabase.block()

    override fun close() = ginaRoomDatabase.close()

    private fun openDb(databasePath: String) {
        val builder = Room.databaseBuilder(context, GinaRoomDatabase::class.java, databasePath)
                .fallbackToDestructiveMigration()
        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }

        ginaRoomDatabase = builder.build()
    }
}

interface IDataManager {
    fun setSourceFile(databasePath: String)
    fun isDbOpen(): Boolean
    fun <T> dao(block: GinaRoomDatabase.() -> T): T
    fun close()
}

