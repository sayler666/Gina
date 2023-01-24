package com.sayler666.gina.settings

import com.sayler666.gina.db.DatabaseSettings
import com.sayler666.gina.imageCompressor.ImageCompressorSettings
import javax.inject.Inject

class Settings @Inject constructor(
    databaseSettings: DatabaseSettings,
    imageCompressorSettings: ImageCompressorSettings
) : DatabaseSettings by databaseSettings,
    ImageCompressorSettings by imageCompressorSettings
