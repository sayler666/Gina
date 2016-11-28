package entity;

/**
 * Created by lchromy on 26.05.15.
 */

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

@DatabaseTable(tableName = "days")
public class Day extends BaseEntity implements Comparable<Day>, Parcelable {
  public static final String DATE_COL = "date";
  @DatabaseField(columnName = DATE_COL, dataType = DataType.DATE_TIME)
  private DateTime date;
  public static final String CONTENT_COL = "content";
  @DatabaseField(columnName = CONTENT_COL)
  private String content;

  public Day() {
  }

  public Day(DateTime date, String content) {
    this.date = date;
    this.content = content;
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
}
