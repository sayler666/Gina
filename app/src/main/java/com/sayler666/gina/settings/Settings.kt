package com.sayler666.gina.settings

import com.sayler666.core.image.ImageOptimizationSettings
import com.sayler666.gina.db.DatabaseSettings
import javax.inject.Inject

class Settings @Inject constructor(
    databaseSettings: DatabaseSettings,
    imageOptimizationSettings: ImageOptimizationSettings,
    appSettings: AppSettings
) : DatabaseSettings by databaseSettings,
    ImageOptimizationSettings by imageOptimizationSettings,
    AppSettings by appSettings
