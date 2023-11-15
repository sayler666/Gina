package com.sayler666.gina.gallery.usecase

import android.content.Context
import android.database.SQLException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import coil.decode.DecodeUtils.calculateInSampleSize
import coil.size.Scale.FILL
import com.sayler666.core.collections.pmap
import com.sayler666.gina.core.BuildConfig
import com.sayler666.gina.db.Attachment
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.returnWithDaysDao
import com.sayler666.gina.db.withDaysDao
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
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val externalScope: CoroutineScope,
    private val context: Context
) : ImageAttachmentsRepository {

    private val attachmentsCached = MutableStateFlow<List<Thumbnail>>(emptyList())

    override val attachment: StateFlow<List<Thumbnail>> = attachmentsCached

    private var offset = 0

    override fun fetchNextPage() {
        externalScope.launch {
            try {
                ginaDatabaseProvider.withDaysDao {
                    val thumbnails = getImageAttachmentsIds(offset)
                        .mapToThumbnails().filterNotNull()

                    val updatedState = (attachmentsCached.value + thumbnails).distinctBy { it.id }
                    attachmentsCached.tryEmit(updatedState)

                    offset = updatedState.size
                }
            } catch (e: SQLException) {
                Timber.e(e, "ImageRepository: Database error")
            }
        }
    }

    override suspend fun fetchFullImage(id: Int): Result<Attachment> = try {
        val image: Attachment? = ginaDatabaseProvider.returnWithDaysDao { getImage(id) }
        image?.let { Result.success(image) }
            ?: Result.failure(NoSuchElementException("Image with id: '$id' not found!"))
    } catch (e: SQLException) {
        Timber.e(e, "Database error")
        Result.failure(e)
    }

    private suspend fun List<Int>.mapToThumbnails(): List<Thumbnail?> =
        pmap { attachmentId ->
            (getThumbnailFromCacheIfExist(attachmentId)
                ?: createThumbnailForId(attachmentId)
                    .also { thumbnail -> thumbnail?.let { saveFile(attachmentId, it) } }
                    )?.let { Thumbnail(it, attachmentId) }
        }

    private fun getThumbnailFromCacheIfExist(id: Int): ByteArray? =
        if (context.fileList().contains(createCacheFileName(id))) loadFile(id) else null

    private suspend fun createThumbnailForId(id: Int): ByteArray? = coroutineScope {
        async {
            ginaDatabaseProvider.returnWithDaysDao {
                val attachment = getImage(id)
                val image = attachment.content ?: return@returnWithDaysDao null
                BitmapFactory.Options().run {
                    inJustDecodeBounds = true
                    BitmapFactory.decodeByteArray(image, 0, image.size, this)

                    inSampleSize = calculateInSampleSize(
                        srcWidth = outWidth,
                        srcHeight = outHeight,
                        dstWidth = THUMBNAIL_SIZE,
                        dstHeight = THUMBNAIL_SIZE,
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
        const val THUMBNAIL_SIZE = 140
        const val THUMBNAIL_QUALITY = 60
    }
}
