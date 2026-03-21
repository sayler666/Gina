package com.sayler666.data.database.db.journal

import android.app.Application
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.core.net.toUri
import androidx.room.InvalidationTracker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class DatabaseFileManager(
    private val application: Application,
    private val databaseSettingsStorage: DatabaseSettingsStorage,
    private val db: GinaDatabase,
    private val syncScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        const val DB_NAME = "gina_journal.db"
        private val TRACKED_TABLES = arrayOf("days", "attachments", "friends", "daysFriends")
    }

    private var syncJob: Job? = null
    private val _dbInvalidations = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val dbInvalidations: SharedFlow<Unit> = _dbInvalidations.asSharedFlow()

    // region File operations

    suspend fun importFromUri(uri: Uri): Boolean = try {
        db.close()
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
        databaseSettingsStorage.saveDatabasePath(localFile.absolutePath)
        true
    } catch (e: Exception) {
        Timber.e(e, "DatabaseFileManager: Error importing DB from URI")
        false
    }

    suspend fun createNewDb(uri: Uri): Boolean = try {
        db.close()
        application.contentResolver.takePersistableUriPermission(
            uri,
            FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
        )
        val localFile = application.getDatabasePath(DB_NAME)
        localFile.parentFile?.mkdirs()
        localFile.delete()
        databaseSettingsStorage.saveExternalDbUri(uri.toString())
        databaseSettingsStorage.saveDatabasePath(localFile.absolutePath)
        true
    } catch (e: Exception) {
        Timber.e(e, "DatabaseFileManager: Error creating new DB")
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
        Timber.i("DatabaseFileManager: DB exported to ${uri.path}")
        true
    } catch (e: Exception) {
        Timber.e(e, "DatabaseFileManager: Error exporting DB to URI")
        false
    }

    // endregion

    // region Sync

    suspend fun sync() {
        val uriString = databaseSettingsStorage.getExternalDbUriFlow().first() ?: return
        exportToUri(uriString.toUri())
    }

    fun setupAutoSync() {
        db.invalidationTracker.addObserver(object : InvalidationTracker.Observer(TRACKED_TABLES) {
            override fun onInvalidated(tables: Set<String>) {
                Timber.d("DatabaseFileManager: onInvalidated")
                _dbInvalidations.tryEmit(Unit)
                scheduleSync()
            }
        })
    }

    private fun scheduleSync() {
        syncJob?.cancel()
        syncJob = syncScope.launch(dispatcher) {
            delay(2_000)
            sync()
        }
    }

    // endregion

    // region Accessors

    fun getDatabaseExternalPathFlow() = databaseSettingsStorage.getExternalDbUriFlow()
        .map { uriString ->
            uriString?.let { val uri = it.toUri(); queryExternalPath(uri) ?: queryDisplayName(uri) }
        }
        .flowOn(Dispatchers.IO)

    fun getLocalDbFile(): File? = application.getDatabasePath(DB_NAME)

    // endregion

    // region URI helpers

    private fun queryDisplayName(uri: Uri): String? = try {
        application.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
    } catch (e: Exception) {
        Timber.e(e, "DatabaseFileManager: Error querying for DB DISPLAY_NAME")
        null
    }

    private fun queryExternalPath(uri: Uri): String? = try {
        val docId = DocumentsContract.getDocumentId(uri)
        val colonIndex = docId.indexOf(':')
        if (colonIndex >= 0 && colonIndex < docId.lastIndex) docId.substring(colonIndex + 1) else null
    } catch (e: Exception) {
        Timber.e(e, "DatabaseFileManager: Error getting External Path")
        null
    }

    // endregion
}
