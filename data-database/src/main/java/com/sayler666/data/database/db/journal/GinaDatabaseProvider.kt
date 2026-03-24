package com.sayler666.data.database.db.journal

import android.app.Application
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.core.net.toUri
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.sayler666.data.database.db.journal.dao.DaysDao
import com.sayler666.data.database.db.journal.dao.RawDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class GinaDatabaseProvider(
    private val application: Application,
    private val databaseSettingsStorage: DatabaseSettingsStorage
) {
    private var databaseInstance: GinaDatabase? = null
    private val dbMutex = Mutex()
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var syncJob: Job? = null
    private val _dbInvalidations = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val dbInvalidations: SharedFlow<Unit> = _dbInvalidations.asSharedFlow()

    companion object {
        const val DB_NAME = "gina_journal.db"
        private val TRACKED_TABLES = arrayOf("days", "attachments", "friends", "daysFriends")
    }

    suspend fun openSavedDB(): Boolean {
        val savedPath = databaseSettingsStorage.getDatabasePathFlow().first() ?: return false
        return try {
            dbMutex.withLock {
                if (databaseInstance == null) {
                    databaseInstance =
                        Room.databaseBuilder(application, GinaDatabase::class.java, savedPath)
                            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                            .addMigrations(GinaDatabase.MIGRATION_1_2)
                            .build()
                    setupAutoSync(databaseInstance!!)
                    Timber.d("GinaDatabaseProvider: Database instance created")
                }
            }
            true
        } catch (e: Exception) {
            Timber.e(e, "GinaDatabaseProvider: Error opening DB")
            false
        }
    }

    suspend fun openAndRememberDB(path: String): Boolean {
        return try {
            dbMutex.withLock {
                if (databaseInstance == null) {
                    databaseInstance =
                        Room.databaseBuilder(application, GinaDatabase::class.java, path)
                            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                            .addMigrations(GinaDatabase.MIGRATION_1_2)
                            .build()
                    setupAutoSync(databaseInstance!!)
                }
            }
            databaseSettingsStorage.saveDatabasePath(path)
            true
        } catch (e: Exception) {
            Timber.e(e, "GinaDatabaseProvider: Error opening DB")
            false
        }
    }

    fun closeDB() {
        databaseInstance?.close()
        databaseInstance = null
    }

    suspend fun importFromUri(uri: Uri): Boolean = try {
        closeDB()
        application.contentResolver.takePersistableUriPermission(
            uri,
            FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
        )
        val localFile = application.getDatabasePath(DB_NAME)
        localFile.parentFile?.mkdirs()
        application.contentResolver.openInputStream(uri)?.use { input ->
            localFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        databaseSettingsStorage.saveExternalDbUri(uri.toString())
        openAndRememberDB(localFile.absolutePath)
    } catch (e: Exception) {
        Timber.e(e, "GinaDatabaseProvider: Error importing DB from URI")
        false
    }

    suspend fun createNewDb(uri: Uri): Boolean = try {
        closeDB()
        application.contentResolver.takePersistableUriPermission(
            uri,
            FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
        )
        val localFile = application.getDatabasePath(DB_NAME)
        localFile.parentFile?.mkdirs()
        localFile.delete()
        databaseSettingsStorage.saveExternalDbUri(uri.toString())
        openAndRememberDB(localFile.absolutePath)
    } catch (e: Exception) {
        Timber.e(e, "GinaDatabaseProvider: Error creating new DB")
        false
    }

    fun exportToUri(uri: Uri): Boolean = try {
        val localFile = application.getDatabasePath(DB_NAME)
        application.contentResolver.openFileDescriptor(uri, "rwt")?.use { pfd ->
            FileOutputStream(pfd.fileDescriptor).use { output ->
                localFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
        }

        Timber.i("GinaDatabaseProvider: DB exported to ${uri.path}")
        true
    } catch (e: Exception) {
        Timber.e(e, "GinaDatabaseProvider: Error exporting DB to URI")
        false
    }

    private fun setupAutoSync(db: GinaDatabase) {
        db.invalidationTracker.addObserver(object : InvalidationTracker.Observer(TRACKED_TABLES) {
            override fun onInvalidated(tables: Set<String>) {
                _dbInvalidations.tryEmit(Unit)
                scheduleSync()
            }
        })
    }

    private fun scheduleSync() {
        syncJob?.cancel()
        syncJob = syncScope.launch {
            delay(2_000)
            sync()
        }
    }

    suspend fun sync() {
        val uriString = databaseSettingsStorage.getExternalDbUriFlow().first() ?: return
        exportToUri(uriString.toUri())
    }

    private fun queryDisplayName(uri: Uri): String? = try {
        application.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
    } catch (e: Exception) {
        null
    }

    private fun queryExternalPath(uri: Uri): String? = try {
        val docId = DocumentsContract.getDocumentId(uri)
        val colonIndex = docId.indexOf(':')
        if (colonIndex >= 0 && colonIndex < docId.lastIndex) docId.substring(colonIndex + 1) else null
    } catch (e: Exception) {
        null
    }

    fun getDatabaseExternalPathFlow() = databaseSettingsStorage.getExternalDbUriFlow()
        .map { uriString ->
            uriString?.let { val uri = it.toUri(); queryExternalPath(uri) ?: queryDisplayName(uri) }
        }
        .flowOn(Dispatchers.IO)

    fun getLocalDbFile(): File? = application.getDatabasePath(DB_NAME)

    fun getOpenedDb(): GinaDatabase? = databaseInstance
}

suspend fun GinaDatabaseProvider.withDaysDao(action: suspend DaysDao.() -> Unit) {
    getOpenedDb()?.daysDao()?.action()
}

suspend fun GinaDatabaseProvider.transactionWithDaysDao(action: suspend DaysDao.() -> Unit) {
    getOpenedDb()?.withTransaction {
        getOpenedDb()?.daysDao()?.action()
    }
}

suspend fun <T> GinaDatabaseProvider.returnWithDaysDao(action: suspend DaysDao.() -> T): T? {
    return getOpenedDb()?.daysDao()?.action()
}

suspend fun GinaDatabaseProvider.withRawDao(action: suspend RawDao.() -> Unit) {
    getOpenedDb()?.rawDao()?.action()
}
