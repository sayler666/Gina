/**
 * Created by sayler on 2016-12-09.
 *
 */
package com.sayler.app2.file

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import okio.BufferedSink
import okio.Okio
import java.io.File
import java.io.IOException

/**

 * @author sayler
 */
object FileUtils {
    @Throws(IOException::class)
    fun openFileIntent(activity: Activity, bytes: ByteArray, mimeType: String, authority: String) {
        val file = File(activity.filesDir.toString() + File.separator + "shared_file")
        val sink: BufferedSink
        sink = Okio.buffer(Okio.sink(file))
        sink.write(bytes)
        sink.close()

        val fileUri = FileProvider.getUriForFile(activity, authority, file)
        val intent = ShareCompat.IntentBuilder.from(activity)
                .setStream(fileUri)
                .setType(mimeType)
                .intent
                .setAction(Intent.ACTION_VIEW)
                .setDataAndType(fileUri, mimeType)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivity(intent)

    }

    fun selectFileIntent(activity: Activity, requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        activity.startActivityForResult(intent, requestCode)
    }

    fun readMimeTypeFromUri(uri: Uri, activity: Activity): String {
        val contentResolver = activity.contentResolver
        return contentResolver.getType(uri)
    }
}
