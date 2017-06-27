/**
 * Created by sayler on 2016-12-09.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.domain;

import android.os.Parcelable;

public interface IAttachment extends Parcelable {

  long getId();

  void setId(long id);

  String getMimeType();

  void setMimeType(String mimeType);

  byte[] getFile();

  void setFile(byte[] file);
}
