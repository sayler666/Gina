package com.sayler.gina.ui;

import android.support.annotation.NonNull;

/**
 * Created by miquido on 28/11/16.
 */

public class TextUtils {
  @NonNull
  public static String truncateTo(String text, int maxLength, String ellipsis) {
    String contentShort;
    if (text.length() > maxLength) {
      contentShort = text.substring(0, maxLength) + ellipsis;
    } else {
      contentShort = text;
    }
    return contentShort;
  }
}
