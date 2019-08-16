package com.sayler.app2.data

import android.content.Context
import android.os.Debug
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase.JournalMode.TRUNCATE
import com.sayler.data.days.GinaRoomDatabase
import com.sayler.data.settings.SettingsData
import com.sayler.data.settings.SettingsRepository
import com.sayler.data.settings.SettingsState
import javax.inject.Inject

class DataManager @Inject constructor(
        private val settingsRepository: SettingsRepository,
        private val context: Context
) : IDataManager {

    private var ginaRoomDatabase: GinaRoomDatabase? = null

    init {
        val settings = settingsRepository.get()
        when (settings) {
            SettingsState.NotSet -> Log.d("DataManager", "Path not set.")
            is SettingsState.Set -> openDb(settings.settingsData.databasePath)
        }
    }

    override fun setSourceFile(databasePath: String) {
        settingsRepository.save(SettingsData(databasePath))
        openDb(databasePath)
    }

    override fun isDbOpen() = settingsRepository.get() is SettingsState.Set

    private fun openDb(databasePath: String) {
        val builder = Room
                .databaseBuilder(context, GinaRoomDatabase::class.java, databasePath)
                .fallbackToDestructiveMigration()
                .setJournalMode(TRUNCATE)

        if (Debug.isDebuggerConnected()) builder.allowMainThreadQueries()

        ginaRoomDatabase = builder.build()

        Log.d("DaysViewModel", "Database opened: $databasePath")
    }

    override fun <T> dao(block: GinaRoomDatabase.() -> T): T? = ginaRoomDatabase?.block()

    override fun close() = ginaRoomDatabase?.close()
}

interface IDataManager {
    fun setSourceFile(databasePath: String)
    fun isDbOpen(): Boolean
    fun <T> dao(block: GinaRoomDatabase.() -> T): T?
    fun close(): Unit?
}

