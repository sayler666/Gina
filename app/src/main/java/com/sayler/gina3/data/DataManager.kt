package com.sayler.gina3.data

import android.content.Context
import android.os.Debug
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase.JournalMode.TRUNCATE
import com.sayler.data.GinaDatabase
import com.sayler.data.GinaDatabaseRoomImpl
import com.sayler.data.dao.EntityDao
import com.sayler.settings.SettingsData
import com.sayler.settings.SettingsRepository
import com.sayler.settings.SettingsState.NotSet
import com.sayler.settings.SettingsState.Set
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class DataManager @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : IDataManager {

    private lateinit var ginaRoomDatabase: GinaDatabase
    private val openDbChannel = ConflatedBroadcastChannel<Boolean>()

    init {
        when (val settings = settingsRepository.get()) {
            NotSet -> Log.d("DataManager", "Path not set.")
            is Set -> {
                Log.d("DataManager", "Path set to: ${settings.settingsData.databasePath}")
                openConnection(settings.settingsData.databasePath)
            }
        }
    }

    override fun setSourceFile(databasePath: String) {
        settingsRepository.save(SettingsData(databasePath))
        openConnection(databasePath)
    }

    override fun isDbOpen() = settingsRepository.get() is Set

    override fun dbOpen(): Flow<Boolean> = openDbChannel
        .asFlow()

    override fun <E, T : EntityDao<E>> dao(block: GinaDatabase.() -> T): T {
        require(isDbOpen()) { "No open connection!" }
        return ginaRoomDatabase.block()
    }

    override fun close() {
        ginaRoomDatabase.closeConnection()
        openDbChannel.sendBlocking(false)
        Log.d("DataManager", "Database closed.")
    }

    private fun openConnection(databasePath: String) {
        val builder = Room
            .databaseBuilder(context, GinaDatabaseRoomImpl::class.java, databasePath)
            .fallbackToDestructiveMigration()
            .setJournalMode(TRUNCATE)

        if (Debug.isDebuggerConnected()) builder.allowMainThreadQueries()

        ginaRoomDatabase = builder.build()
        openDbChannel.sendBlocking(isDbOpen())
        Log.d("DataManager", "Database opened: $databasePath")
    }
}

interface IDataManager {
    fun setSourceFile(databasePath: String)
    fun <E, T : EntityDao<E>> dao(block: GinaDatabase.() -> T): T
    fun close()
    fun isDbOpen(): Boolean
    fun dbOpen(): Flow<Boolean>
}