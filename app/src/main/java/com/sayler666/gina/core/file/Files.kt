package com.sayler666.gina.core.file

import android.content.Context
import android.content.Intent
import android.net.Uri
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

    fun readBytesAndMimeTypeFromUri(uri: Uri, context: Context): Pair<ByteArray, String>{
        val fileBytes = readFileFromUri(uri, context)
        val mimeType = readMimeTypeFromUri(uri, context) ?: "*/*"
        return fileBytes to mimeType
    }
}