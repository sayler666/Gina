package com.sayler666.core.image

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeByteArray
import id.zelory.compressor.BuildConfig
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ImageOptimization @Inject constructor(
    private val app: Application,
    private val imageOptimizationSettings: ImageOptimizationSettings
) {

    suspend fun compressImage(
        bytes: ByteArray
    ): ByteArray = withContext(Dispatchers.IO) {
        val compressorSettings = imageOptimizationSettings.getImageCompressorSettingsFlow()
            .first()

        if (BuildConfig.DEBUG) {
            Timber.d("ImageOptimization settings: $compressorSettings")
            val original = decodeByteArray(bytes, 0, bytes.size)
            Timber.d("ImageOptimization: Original image res: ${original.width}/${original.height}, size: ${bytes.size} bytes")
        }

        if (compressorSettings.compressionEnabled) {
            val resized = resize(bytes, compressorSettings)
            compress(resized, compressorSettings)
        } else {
            bytes
        }
    }

    private suspend fun resize(
        bytes: ByteArray,
        compressorSettings: OptimizationSettings
    ): ByteArray = withContext(Dispatchers.IO) {
        val original = decodeByteArray(bytes, 0, bytes.size)
        val (width, height) = original.width to original.height
        val ratio = width.toFloat() / height.toFloat()
        val newWidth = (compressorSettings.height * ratio).toInt()
        val resized = Bitmap.createScaledBitmap(original, newWidth, compressorSettings.height, true)

        return@withContext ByteArrayOutputStream().use { stream ->
            resized.compress(Bitmap.CompressFormat.JPEG, compressorSettings.quality, stream)
            val resizedBytes = stream.toByteArray()
            if (BuildConfig.DEBUG) {
                val resizedImage = decodeByteArray(resizedBytes, 0, resizedBytes.size)
                Timber.d("ImageOptimization: Resized image res: ${resizedImage.width}/${resizedImage.height}, size: ${resizedImage.byteCount} bytes")
                resizedImage.recycle()
            }
            original.recycle()
            resized.recycle()
            resizedBytes
        }
    }

    private suspend fun compress(
        bytes: ByteArray,
        optimizationSettings: OptimizationSettings
    ): ByteArray = withContext(Dispatchers.IO) {
        val file = File.createTempFile("file", ".tmp")

        FileOutputStream(file).use { fos ->
            fos.write(bytes)
            fos.close()
        }

        val compressedBytes = Compressor.compress(app, file) {
            resolution(optimizationSettings.width, optimizationSettings.height)
            quality(optimizationSettings.quality)
        }.readBytes()

        if (BuildConfig.DEBUG) {
            val compressedBitmap = decodeByteArray(compressedBytes, 0, compressedBytes.size)
            Timber.d("ImageOptimization: Compressed image res: ${compressedBitmap.width}/${compressedBitmap.height}, size: ${compressedBytes.size} bytes")
            compressedBitmap.recycle()
        }
        file.delete()
        compressedBytes
    }

    @Serializable
    data class OptimizationSettings(
        val compressionEnabled: Boolean = true,
        val width: Int = IMAGE_WIDTH,
        val height: Int = IMAGE_HEIGHT,
        val quality: Int = IMAGE_QUALITY,
        val size: Long = IMAGE_SIZE
    )

    companion object {
        const val IMAGE_WIDTH = 1920
        const val IMAGE_HEIGHT = 1080
        const val IMAGE_QUALITY = 50
        const val IMAGE_SIZE = 200_000L
    }
}
