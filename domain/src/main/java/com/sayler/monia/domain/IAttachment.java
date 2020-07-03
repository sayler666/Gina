/**
 * Created by sayler on 2016-12-09.
 * <p>

 */
package com.sayler.monia.domain;

import android.os.Parcelable;

public interface IAttachment extends Parcelable {

  long getId();

  void setId(long id);

  String getMimeType();

  void setMimeType(String mimeType);

  byte[] getFile();

  void setFile(byte[] file);


}
