package com.sayler666.gina.settings

import com.sayler666.gina.db.DatabaseSettings
import com.sayler666.gina.imageCompressor.ImageOptimizationSettings
import javax.inject.Inject

class Settings @Inject constructor(
    databaseSettings: DatabaseSettings,
    imageOptimizationSettings: ImageOptimizationSettings
) : DatabaseSettings by databaseSettings,
    ImageOptimizationSettings by imageOptimizationSettings
