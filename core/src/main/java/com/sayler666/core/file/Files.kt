package com.sayler666.core.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.sayler666.gina.core.BuildConfig
import okio.BufferedSink
import okio.buffer
import okio.sink
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object Files {
    fun selectFileIntent(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        return intent
    }

    fun openFileIntent(
        context: Context,
        bytes: ByteArray,
        mimeType: String
    ) {
        val file = File(context.filesDir.toString() + File.separator + "shared_file")
        val sink: BufferedSink = file.sink().buffer()
        sink.write(bytes)
        sink.close()

        val fileUri = FileProvider.getUriForFile(context, BuildConfig.FILE_PROVIDER_AUTHORITY, file)
        val intent = ShareCompat.IntentBuilder(context)
            .setStream(fileUri)
            .setType(mimeType)
            .intent
            .setAction(Intent.ACTION_VIEW)
            .setDataAndType(fileUri, mimeType)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        context.startActivity(intent)
    }

    fun shareImageFile(
        context: Context,
        file: File
    ) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            BuildConfig.FILE_PROVIDER_AUTHORITY,
            file
        )

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

    fun saveByteArrayToFile(context: Context, byteArray: ByteArray, fileName: String): File? {
        val file = File(context.externalCacheDir, fileName)
        try {
            FileOutputStream(file).use { out ->
                out.write(byteArray)
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    private fun readFileFromUri(uri: Uri, context: Context): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        var data = ByteArray(0)
        if (inputStream != null) data = IOUtils.toByteArray(inputStream)

        return data
    }

    private fun readMimeTypeFromUri(uri: Uri, context: Context): String? {
        val contentResolver = context.contentResolver
        return contentResolver.getType(uri)
    }

    fun readBytesAndMimeTypeFromUri(uri: Uri, context: Context): Pair<ByteArray, String> {
        val fileBytes = readFileFromUri(uri, context)
        val mimeType = readMimeTypeFromUri(uri, context) ?: "*/*"
        return fileBytes to mimeType
    }
}

fun handleMultipleVisualMedia(
    uri: List<Uri?>,
    context: Context,
    applyAvatar: (List<Pair<ByteArray, String>>) -> Unit
) {
    fun createAttachment(uri: Uri): Pair<ByteArray, String> {
        val (content, mimeType) = Files.readBytesAndMimeTypeFromUri(uri, context)
        return content to mimeType
    }

    val attachmentList = mutableListOf<Pair<ByteArray, String>>()
    uri.filterNotNull().forEach { uri ->
        attachmentList.add(createAttachment(uri))
    }
    applyAvatar(attachmentList)
}

fun String.isImageMimeType() = this.contains(IMAGE_MIME_TYPE_PREFIX)

const val IMAGE_MIME_TYPE_PREFIX = "image/"
