package com.sayler666.gina.imageCompressor

import android.app.Application
import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.sayler666.gina.imageCompressor.ImageCompressor.CompressorSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

interface ImageCompressorSettings {
    fun getImageCompressorSettingsFlow(): Flow<CompressorSettings>
    suspend fun saveImageCompressorSettings(compressorSettings: CompressorSettings)
    suspend fun resetImageCompressorSettings()
}

class ImageCompressorSettingsImpl @Inject constructor(private val app: Application) :
    ImageCompressorSettings {

    private val Context.dataStore by dataStore(
        "$PREFERENCES_NAME.json",
        ImageCompressorSettingsSerializer
    )

    override fun getImageCompressorSettingsFlow(): Flow<CompressorSettings> = app.dataStore.data

    override suspend fun saveImageCompressorSettings(compressorSettings: CompressorSettings) {
        app.dataStore.updateData { compressorSettings }
    }

    override suspend fun resetImageCompressorSettings() {
        app.dataStore.updateData { CompressorSettings() }
    }

    companion object {
        const val PREFERENCES_NAME = "IMAGE_COMPRESSOR_PREFERENCES"
    }
}

object ImageCompressorSettingsSerializer : Serializer<CompressorSettings> {

    override val defaultValue = CompressorSettings()

    override suspend fun readFrom(input: InputStream): CompressorSettings {
        try {
            return Json.decodeFromString(
                CompressorSettings.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read UserPrefs", serialization)
        }
    }

    override suspend fun writeTo(t: CompressorSettings, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(CompressorSettings.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}
