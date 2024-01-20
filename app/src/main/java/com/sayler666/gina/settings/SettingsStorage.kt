package com.sayler666.gina.settings

import com.sayler666.core.image.ImageOptimizationSettings
import com.sayler666.gina.db.DatabaseSettingsStorage
import javax.inject.Inject

class SettingsStorage @Inject constructor(
    databaseSettingsStorage: DatabaseSettingsStorage,
    imageOptimizationSettings: ImageOptimizationSettings,
    appSettings: AppSettings
) : DatabaseSettingsStorage by databaseSettingsStorage,
    ImageOptimizationSettings by imageOptimizationSettings,
    AppSettings by appSettings
