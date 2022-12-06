package com.sayler666.gina

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class GinaApplication : Application() {
    val applicationScope : CoroutineScope = CoroutineScope(SupervisorJob())
}
