package com.sayler666.gina.imageCompressor

import android.app.Application
import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.sayler666.gina.imageCompressor.ImageOptimization.OptimizationSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

interface ImageOptimizationSettings {
    fun getImageCompressorSettingsFlow(): Flow<OptimizationSettings>
    suspend fun saveImageCompressorSettings(optimizationSettings: OptimizationSettings)
    suspend fun resetImageCompressorSettings()
}

class ImageOptimizationSettingsImpl @Inject constructor(private val app: Application) :
    ImageOptimizationSettings {

    private val Context.dataStore by dataStore(
        "$PREFERENCES_NAME.json",
        ImageCompressorSettingsSerializer
    )

    override fun getImageCompressorSettingsFlow(): Flow<OptimizationSettings> = app.dataStore.data

    override suspend fun saveImageCompressorSettings(optimizationSettings: OptimizationSettings) {
        app.dataStore.updateData { optimizationSettings }
    }

    override suspend fun resetImageCompressorSettings() {
        app.dataStore.updateData { OptimizationSettings() }
    }

    companion object {
        const val PREFERENCES_NAME = "IMAGE_COMPRESSOR_PREFERENCES"
    }
}

object ImageCompressorSettingsSerializer : Serializer<OptimizationSettings> {

    override val defaultValue = OptimizationSettings()

    override suspend fun readFrom(input: InputStream): OptimizationSettings {
        try {
            return Json.decodeFromString(
                OptimizationSettings.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read UserPrefs", serialization)
        }
    }

    override suspend fun writeTo(t: OptimizationSettings, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(OptimizationSettings.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}
