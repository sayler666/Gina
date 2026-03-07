package com.sayler666.gina.feature.settings

import com.sayler666.core.image.ImageOptimizationSettings
import com.sayler666.data.database.db.journal.DatabaseSettingsStorage
import javax.inject.Inject

class SettingsStorage @Inject constructor(
    databaseSettingsStorage: DatabaseSettingsStorage,
    imageOptimizationSettings: ImageOptimizationSettings,
    appSettings: AppSettings
) : DatabaseSettingsStorage by databaseSettingsStorage,
    ImageOptimizationSettings by imageOptimizationSettings,
    AppSettings by appSettings
