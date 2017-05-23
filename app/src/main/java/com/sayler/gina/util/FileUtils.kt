/**
 * Created by sayler on 2016-12-09.
 *
 *
 * Copyright 2016 MiQUiDO <http:></http:>//www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.support.v4.app.ShareCompat
import android.support.v4.content.FileProvider
import okio.BufferedSink
import okio.Okio
import org.apache.commons.io.IOUtils

import java.io.File
import java.io.IOException
import java.io.InputStream

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

    @Throws(IOException::class)
    fun readFileFromUri(uri: Uri, activity: Activity): ByteArray {
        val inputStream = activity.contentResolver.openInputStream(uri)
        var data: ByteArray = ByteArray(0)
        if (inputStream != null) data = IOUtils.toByteArray(inputStream)

        return data
    }

    fun readMimeTypeFromUri(uri: Uri, activity: Activity): String {
        val contentResolver = activity.contentResolver
        return contentResolver.getType(uri)
    }
}
