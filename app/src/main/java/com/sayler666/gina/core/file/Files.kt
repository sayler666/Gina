package com.sayler666.gina.core.file

import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File

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
}
