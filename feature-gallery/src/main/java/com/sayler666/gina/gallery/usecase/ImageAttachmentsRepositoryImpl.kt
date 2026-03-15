package com.sayler666.gina.gallery.usecase

import android.content.Context
import android.database.SQLException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import coil.decode.DecodeUtils.calculateInSampleSize
import coil.size.Scale.FILL
import com.sayler666.core.collections.pmap
import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.data.database.db.journal.entity.AttachmentIdWithDate
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.gina.feature.gallery.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject


interface ImageAttachmentsRepository {
    fun fetchNextPage()
    suspend fun fetchFullImage(id: Int): Result<Attachment>

    val attachment: StateFlow<List<Thumbnail>>
}

class ImageAttachmentsRepositoryImpl @Inject constructor(
    private val journalRepository: JournalRepository,
    private val externalScope: CoroutineScope,
    private val context: Context
) : ImageAttachmentsRepository {

    private val attachmentsCached = MutableStateFlow<List<Thumbnail>>(emptyList())

    override val attachment: StateFlow<List<Thumbnail>> = attachmentsCached

    private var offset = 0

    override fun fetchNextPage() {
        externalScope.launch {
            val thumbnails = journalRepository.getImageAttachmentsIds(offset)
                .mapToThumbnails().filterNotNull()

            val updatedState = (attachmentsCached.value + thumbnails).distinctBy { it.id }
            attachmentsCached.tryEmit(updatedState)

            offset = updatedState.size
        }
    }

    override suspend fun fetchFullImage(id: Int): Result<Attachment> = try {
        val image: Attachment? = journalRepository.getImage(id)
        image?.let { Result.success(image) }
            ?: Result.failure(NoSuchElementException("Image with id: '$id' not found!"))
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }

    private fun decodeAspectRatio(bytes: ByteArray): Float {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        return if (options.outWidth > 0 && options.outHeight > 0)
            options.outWidth.toFloat() / options.outHeight.toFloat()
        else 1f
    }

    private suspend fun List<AttachmentIdWithDate>.mapToThumbnails(): List<Thumbnail?> =
        pmap { attachment ->
            val attachmentId = attachment.id
            (getThumbnailFromCacheIfExist(attachmentId)
                ?: createThumbnailForId(attachmentId)
                    .also { thumbnail -> thumbnail?.let { saveFile(attachmentId, it) } }
                    )?.let { bytes -> Thumbnail(bytes, attachmentId, decodeAspectRatio(bytes), attachment.date) }
        }

    private fun getThumbnailFromCacheIfExist(id: Int): ByteArray? =
        if (context.fileList().contains(createCacheFileName(id))) loadFile(id) else null

    private suspend fun createThumbnailForId(id: Int): ByteArray? = coroutineScope {
        async {
            val attachment = journalRepository.getImage(id)
            val image = attachment?.content ?: return@async null
            BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeByteArray(image, 0, image.size, this)
                val ratio = (1.0 * outWidth) / outHeight
                inSampleSize = calculateInSampleSize(
                    srcWidth = outWidth,
                    srcHeight = outHeight,
                    dstWidth = THUMBNAIL_SIZE,
                    dstHeight = (THUMBNAIL_SIZE * ratio).toInt(),
                    scale = FILL
                )

                inJustDecodeBounds = false
                Triple<Int, Int, Bitmap>(
                    outWidth,
                    outHeight,
                    BitmapFactory.decodeByteArray(image, 0, image.size, this)
                )
            }.let { (width, height, bitmap) ->
                ByteArrayOutputStream().use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, THUMBNAIL_QUALITY, stream)
                    val resizedBytes = stream.toByteArray()
                    if (BuildConfig.DEBUG) {
                        val resizedImage =
                            BitmapFactory.decodeByteArray(resizedBytes, 0, resizedBytes.size)
                        Timber.d(
                            "ImageRepository: Resized image res: " +
                                    "${resizedImage.width}/${resizedImage.height}" +
                                    " origin: ${width}/${height}" +
                                    " size: ${resizedBytes.size} bytes"
                        )
                        resizedImage.recycle()
                    }
                    bitmap.recycle()
                    resizedBytes
                }
            }
        }.await()
    }

    private fun saveFile(id: Int, imageBytes: ByteArray): Boolean {
        val currentlyStoredFiles = context.fileList()
        return if (!currentlyStoredFiles.contains(createCacheFileName(id))) {
            try {
                val fos = context.openFileOutput(createCacheFileName(id), Context.MODE_PRIVATE)
                fos.write(imageBytes)
                fos.close()
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        } else {
            Timber.d("ImageRepository: Already stored: $id")
            false
        }
    }

    private fun loadFile(id: Int): ByteArray? = try {
        val fis = context.openFileInput(createCacheFileName(id))
        val bytes = fis.readBytes()
        fis.close()
        bytes
    } catch (e: IOException) {
        Timber.e(e, "Can't load file $id.jpg from internal storage")
        null
    }

    private fun createCacheFileName(id: Int) = "$id.jpg"

    companion object {
        const val THUMBNAIL_SIZE = 250
        const val THUMBNAIL_QUALITY = 80
    }
}
