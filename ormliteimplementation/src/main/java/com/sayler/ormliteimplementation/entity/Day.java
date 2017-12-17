package com.sayler.ormliteimplementation.entity;

/**
 * Created by lchromy on 26.05.15.
 */

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.sayler.gina.domain.IAttachment;
import com.sayler.gina.domain.IDay;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;

@DatabaseTable(tableName = "days")
public class Day extends BaseEntity implements Comparable<Day>, Parcelable, IDay {
  public static final String DATE_COL = "date";
  @DatabaseField(columnName = DATE_COL, dataType = DataType.DATE_TIME)
  private DateTime date;
  public static final String CONTENT_COL = "content";
  @DatabaseField(columnName = CONTENT_COL)
  private String content;
  @ForeignCollectionField
  private ForeignCollection<Attachment> attachments;

  public Day() {
  }

  public Day(DateTime date) {
    this.date = date;
  }

  public Day(DateTime date, String content) {
    this.date = date;
    this.content = content;
  }

  @Override
  public Collection<IAttachment> getAttachments() {
    if (attachments != null) {
      Collection<IAttachment> iAttachments = new ArrayList<>();
      for (Attachment attachment : attachments) {
        iAttachments.add(attachment);
      }
      return iAttachments;
    } else {
      return new ArrayList<>();
    }
  }

  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public int compareTo(Day day) {
    return this.getDate().compareTo(day.getDate());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeSerializable(this.date);
    dest.writeString(this.content);
  }

  protected Day(Parcel in) {
    this.date = (DateTime) in.readSerializable();
    this.content = in.readString();
  }

  public static final Creator<Day> CREATOR = new Creator<Day>() {
    @Override
    public Day createFromParcel(Parcel source) {
      return new Day(source);
    }

    @Override
    public Day[] newArray(int size) {
      return new Day[size];
    }
  };

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Day)) {
      return false;
    }

    Day day = (Day) o;

    if (date != null ? !date.equals(day.date) : day.date != null) {
      return false;
    }
    if (content != null ? !content.equals(day.content) : day.content != null) {
      return false;
    }
    return attachments != null ? attachments.equals(day.attachments) : day.attachments == null;
  }

  @Override
  public int hashCode() {
    int result = date != null ? date.hashCode() : 0;
    result = 31 * result + (content != null ? content.hashCode() : 0);
    result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
    return result;
  }
}
