package com.sayler.ormliteimplementation.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sayler.gina.domain.IAttachment;

import java.util.Arrays;

/**
 * Created by miquido on 08/12/16.
 */

@DatabaseTable(tableName = "attachments")
public class Attachment extends BaseEntity implements Parcelable, IAttachment {

  public static final String FILE_COL = "file";
  @DatabaseField(columnName = FILE_COL, dataType = DataType.BYTE_ARRAY)
  private byte[] file;
  public static final String MIME_TYPE_COL = "mime_type";
  @DatabaseField(columnName = MIME_TYPE_COL)
  private String mimeType;
  public static final String DAYS_ID_COL = "days_id";
  @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = DAYS_ID_COL)
  private Day day;

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public Day getDay() {
    return day;
  }

  public void setDay(Day day) {
    this.day = day;
  }

  public byte[] getFile() {
    return file;
  }

  public void setFile(byte[] file) {
    this.file = file;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Attachment)) {
      return false;
    }

    Attachment that = (Attachment) o;

    if (!Arrays.equals(file, that.file)) {
      return false;
    }
    if (!mimeType.equals(that.mimeType)) {
      return false;
    }
    return day != null ? day.equals(that.day) : that.day == null;

  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(file);
    result = 31 * result + mimeType.hashCode();
    result = 31 * result + (day != null ? day.hashCode() : 0);
    return result;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByteArray(this.file);
    dest.writeParcelable(this.day, flags);
  }

  public Attachment() {
  }

  protected Attachment(Parcel in) {
    this.file = in.createByteArray();
    this.day = in.readParcelable(Day.class.getClassLoader());
  }

  public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {
    @Override
    public Attachment createFromParcel(Parcel source) {
      return new Attachment(source);
    }

    @Override
    public Attachment[] newArray(int size) {
      return new Attachment[size];
    }
  };
}
