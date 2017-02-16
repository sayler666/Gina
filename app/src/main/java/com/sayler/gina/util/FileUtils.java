/**
 * Created by sayler on 2016-12-09.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import okio.BufferedSink;
import okio.Okio;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO Add class description...
 *
 * @author sayler
 */
public class FileUtils {
  public static void openFileIntent(Activity activity, byte[] bytes, String mimeType, String authority) throws IOException {
    File file = new File(activity.getFilesDir() + File.separator + "shared_file");
    BufferedSink sink;
    sink = Okio.buffer(Okio.sink(file));
    sink.write(bytes);
    sink.close();

    Uri fileUri = FileProvider.getUriForFile(activity, authority, file);
    Intent intent = ShareCompat.IntentBuilder.from(activity)
        .setStream(fileUri)
        .setType(mimeType)
        .getIntent()
        .setAction(Intent.ACTION_VIEW)
        .setDataAndType(fileUri, mimeType)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    activity.startActivity(intent);

  }

  public static void selectFileIntent(Activity activity, int requestCode) {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("*/*");
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    activity.startActivityForResult(intent, requestCode);
  }

  public static byte[] readFileFromUri(Uri uri, Activity activity) throws IOException {
    InputStream inputStream = activity.getContentResolver().openInputStream(uri);
    byte[] data = new byte[0];
    if (inputStream != null) {
      data = IOUtils.toByteArray(inputStream);
    }

    return data;
  }

  public static String readMimeTypeFromUri(Uri uri, Activity activity) {
    ContentResolver contentResolver = activity.getContentResolver();
    return contentResolver.getType(uri);
  }
}
