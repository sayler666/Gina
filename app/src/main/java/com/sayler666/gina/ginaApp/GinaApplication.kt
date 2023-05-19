package com.sayler666.gina.ginaApp

import android.app.Application
import com.sayler666.gina.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class GinaApplication : Application() {
    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
    }
}
