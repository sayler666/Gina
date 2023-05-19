package com.sayler666.core.file

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import okio.BufferedSink
import okio.buffer
import okio.sink
import org.apache.commons.io.IOUtils
import java.io.File
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
        mimeType: String,
        authority: String = "com.sayler666.gina.provider"
    ) {
        val file = File(context.filesDir.toString() + File.separator + "shared_file")
        val sink: BufferedSink = file.sink().buffer()
        sink.write(bytes)
        sink.close()

        val fileUri = FileProvider.getUriForFile(context, authority, file)
        val intent = ShareCompat.IntentBuilder(context)
            .setStream(fileUri)
            .setType(mimeType)
            .intent
            .setAction(Intent.ACTION_VIEW)
            .setDataAndType(fileUri, mimeType)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        context.startActivity(intent)
    }

    @Throws(IOException::class)
    private fun readFileFromUri(uri: Uri, context: Context): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        var data: ByteArray = ByteArray(0)
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

fun handleSelectedFiles(
    it: ActivityResult,
    context: Context,
    addAttachments: (List<Pair<ByteArray, String>>) -> Unit
) {

    fun createAttachment(uri: Uri): Pair<ByteArray, String> {
        val (content, mimeType) = Files.readBytesAndMimeTypeFromUri(uri, context)
        return content to mimeType
    }

    if (it.resultCode != Activity.RESULT_CANCELED && it.data != null) {
        // multiple files
        val multipleItems = it.data?.clipData
        val attachmentsList = mutableListOf<Pair<ByteArray, String>>()
        if (multipleItems != null) {
            for (i in 0 until multipleItems.itemCount) {
                attachmentsList += createAttachment(multipleItems.getItemAt(i).uri)
            }
        } else {
            // single file
            it.data?.data?.let { uri ->
                attachmentsList += createAttachment(uri)
            }
        }
        if (attachmentsList.isNotEmpty()) addAttachments(attachmentsList)
    }
}

fun String.isImageMimeType() =
    this.contains(IMAGE_MIME_TYPE_PREFIX)

const val IMAGE_MIME_TYPE_PREFIX = "image/"
