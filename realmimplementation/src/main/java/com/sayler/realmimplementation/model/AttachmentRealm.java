package com.sayler.realmimplementation.model;

/**
 * Created by lchromy on 26.05.15.
 */

import android.os.Parcel;
import android.os.Parcelable;
import com.sayler.gina.domain.IAttachment;
import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

import java.util.Arrays;

@RealmClass
public class AttachmentRealm implements RealmModel, IAttachment, Parcelable {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AttachmentRealm)) {
      return false;
    }

    AttachmentRealm that = (AttachmentRealm) o;

    if (id != that.id) {
      return false;
    }
    if (!Arrays.equals(file, that.file)) {
      return false;
    }
    return mimeType.equals(that.mimeType);

  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + Arrays.hashCode(file);
    result = 31 * result + mimeType.hashCode();
    return result;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeByteArray(this.file);
    dest.writeString(this.mimeType);
  }

  protected AttachmentRealm(Parcel in) {
    this.id = in.readLong();
    this.file = in.createByteArray();
    this.mimeType = in.readString();
  }

  public static final Creator<AttachmentRealm> CREATOR = new Creator<AttachmentRealm>() {
    @Override
    public AttachmentRealm createFromParcel(Parcel source) {
      return new AttachmentRealm(source);
    }

    @Override
    public AttachmentRealm[] newArray(int size) {
      return new AttachmentRealm[size];
    }
  };
}
