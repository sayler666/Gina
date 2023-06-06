package com.sayler666.core.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeByteArray
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import com.sayler666.gina.core.BuildConfig
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
    private val imageOptimizationSettings: ImageOptimizationSettings
) {

    suspend fun optimizeImage(
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
            resize(bytes, compressorSettings)
        } else {
            bytes
        }
    }

    private suspend fun resize(
        bytes: ByteArray,
        compressorSettings: OptimizationSettings
    ): ByteArray = withContext(Dispatchers.IO) {
        val fileTmp = File.createTempFile("file", ".tmp")
        FileOutputStream(fileTmp).use { fos ->
            fos.write(bytes)
            fos.close()
        }

        val orientation = with(ExifInterface(fileTmp.absolutePath)) {
            fileTmp.delete()
            getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
        }

        val original = decodeByteArray(bytes, 0, bytes.size)
        val (width, height) = original.width to original.height
        val ratio = width.toFloat() / height.toFloat()
        val dstWidth = (compressorSettings.height * ratio).toInt()

        val resized = createScaledBitmap(original, dstWidth, compressorSettings.height, orientation)

        return@withContext ByteArrayOutputStream().use { stream ->
            resized.compress(Bitmap.CompressFormat.JPEG, compressorSettings.quality, stream)
            val resizedBytes = stream.toByteArray()
            if (BuildConfig.DEBUG) {
                val resizedImage = decodeByteArray(resizedBytes, 0, resizedBytes.size)
                Timber.d("ImageOptimization: Resized image res: ${resizedImage.width}/${resizedImage.height}, size: ${resizedBytes.size} bytes")
                resizedImage.recycle()
            }
            original.recycle()
            resized.recycle()
            resizedBytes
        }
    }

    private fun createScaledBitmap(
        src: Bitmap,
        dstWidth: Int,
        dstHeight: Int,
        orientation: Int
    ): Bitmap {
        val m = Matrix()
        val width = src.width
        val height = src.height
        if (width != dstWidth || height != dstHeight) {
            val sx = dstWidth / width.toFloat()
            val sy = dstHeight / height.toFloat()
            m.setScale(sx, sy)
        }
        when (orientation) {
            6 -> m.postRotate(90f)
            3 -> m.postRotate(180f)
            8 -> m.postRotate(270f)
        }
        return Bitmap.createBitmap(src, 0, 0, width, height, m, true)
    }

    @Serializable
    data class OptimizationSettings(
        val compressionEnabled: Boolean = true,
        val width: Int = IMAGE_WIDTH,
        val height: Int = IMAGE_HEIGHT,
        val quality: Int = IMAGE_QUALITY
    )

    companion object {
        const val IMAGE_WIDTH = 1920
        const val IMAGE_HEIGHT = 1080
        const val IMAGE_QUALITY = 50
    }
}
