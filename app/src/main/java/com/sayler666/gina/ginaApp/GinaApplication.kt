package com.sayler666.gina.ginaApp

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sayler666.data.database.db.journal.DatabaseFileManager
import com.sayler666.gina.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

@HiltAndroidApp
class GinaApplication : Application() {
    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var databaseFileManager: DatabaseFileManager

    override fun onCreate() {
        super.onCreate()

        databaseFileManager.setupAutoSync()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }
}

private class CrashlyticsTree : Timber.Tree() {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        crashlytics.log("${priorityLabel(priority)}/$tag: $message")
        t?.let { crashlytics.recordException(it) }
    }

    private fun priorityLabel(priority: Int) = when (priority) {
        android.util.Log.VERBOSE -> "V"
        android.util.Log.DEBUG -> "D"
        android.util.Log.INFO -> "I"
        android.util.Log.WARN -> "W"
        android.util.Log.ERROR -> "E"
        android.util.Log.ASSERT -> "A"
        else -> "?"
    }
}
