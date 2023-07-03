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
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.returnWithDaysDao
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
    private val databaseProvider: DatabaseProvider,
    private val externalScope: CoroutineScope,
    private val context: Context
) : ImageAttachmentsRepository {

    private val attachmentsCached = MutableStateFlow<List<Thumbnail>>(emptyList())

    override val attachment: StateFlow<List<Thumbnail>>
        get() = attachmentsCached

    private var offset = 0

    override fun fetchNextPage() {
        externalScope.launch {
            try {
                databaseProvider.getOpenedDb()?.let { db ->
                    val thumbnails = db.daysDao().getImageAttachmentsIds(offset)
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
        val image: Attachment? = databaseProvider.returnWithDaysDao { getImage(id) }
        image?.let { Result.success(image) }
            ?: Result.failure(NoSuchElementException("No previous day found"))
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

    private fun getThumbnailFromCacheIfExist(id: Int): ByteArray? {
        val currentlyStoredFiles = context.fileList()
        return if (currentlyStoredFiles.contains("$id.jpg")) {
            loadFile(id)
        } else {
            null
        }
    }

    private suspend fun createThumbnailForId(id: Int): ByteArray? = coroutineScope {
        async {
            databaseProvider.getOpenedDb()?.let {
                val attachment = it.daysDao().getImage(id)
                val image = attachment.content ?: return@async null
                BitmapFactory.Options().run {
                    inJustDecodeBounds = true
                    BitmapFactory.decodeByteArray(image, 0, image.size, this)

                    val imageHeight: Int = outHeight
                    val imageWidth: Int = outWidth
                    inSampleSize = calculateInSampleSize(imageWidth, imageHeight, 100, 100, FILL)

                    inJustDecodeBounds = false
                    BitmapFactory.decodeByteArray(image, 0, image.size, this)
                }.let { thumbnail ->
                    ByteArrayOutputStream().use { stream ->
                        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        val resizedBytes = stream.toByteArray()
                        if (BuildConfig.DEBUG) {
                            val resizedImage =
                                BitmapFactory.decodeByteArray(resizedBytes, 0, resizedBytes.size)
                            Timber.d("ImageRepository: Resized image res: ${resizedImage.width}/${resizedImage.height}, size: ${resizedBytes.size} bytes")
                            resizedImage.recycle()
                        }
                        thumbnail.recycle()
                        resizedBytes
                    }
                }
            }
        }.await()
    }

    private fun saveFile(id: Int, imageBytes: ByteArray): Boolean {
        val currentlyStoredFiles = context.fileList()
        return if (!currentlyStoredFiles.contains("$id.jpg")) {
            try {
                val fos = context.openFileOutput("$id.jpg", Context.MODE_PRIVATE)
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
        val fis = context.openFileInput("$id.jpg")
        val bytes = fis.readBytes()
        fis.close()
        bytes
    } catch (e: IOException) {
        Timber.e(e, "Can't load file $id.jpg from internal storage")
        null
    }
}
