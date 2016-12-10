package com.sayler.gina.domain.realm.model;

/**
 * Created by lchromy on 26.05.15.
 */

import com.sayler.gina.domain.IAttachment;
import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
public class AttachmentRealm implements RealmModel, IAttachment {
  private long id = -1;
  private byte[] file;
  private String mimeType;

  public AttachmentRealm() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public byte[] getFile() {
    return file;
  }

  public void setFile(byte[] file) {
    this.file = file;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }
}
