package com.sayler666.gina.imageCompressor

import android.app.Application
import android.graphics.BitmapFactory
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ImageCompressor @Inject constructor(
    private val app: Application,
    private val imageCompressorSettings: ImageCompressorSettings
) {

    suspend fun compressImage(
        bytes: ByteArray
    ): ByteArray = coroutineScope {
        withContext(Dispatchers.IO) {
            val compressorSettings =
                imageCompressorSettings.getImageCompressorSettingsFlow().first()
            Timber.d("Compress: Compressor settings: $compressorSettings")

            val original = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            Timber.d("Compress: Original image res: ${original.width}/${original.height}, size: ${bytes.size} bytes")

            val file = withContext(Dispatchers.IO) {
                File.createTempFile("file", ".tmp")
            }

            val fos = FileOutputStream(file)
            fos.write(bytes)
            fos.close()

            val compressedBytes = Compressor.compress(app, file) {
                resolution(compressorSettings.width, compressorSettings.height)
                quality(compressorSettings.quality)
                size(compressorSettings.size)
            }.readBytes()

            val compressed = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
            Timber.d("Compress: Compressed image res: ${compressed.width}/${compressed.height}, size: ${compressedBytes.size} bytes")

            compressedBytes
        }
    }

    @Serializable
    data class CompressorSettings(
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
