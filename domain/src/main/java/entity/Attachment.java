package entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by miquido on 08/12/16.
 */

@DatabaseTable(tableName = "attachments")
public class Attachment extends BaseEntity implements Parcelable {

  public static final String FILE_COL = "file";
  @DatabaseField(columnName = FILE_COL, dataType = DataType.BYTE_ARRAY)
  private byte[] file;
  public static final String DAYS_ID_COL = "days_id";
  @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = DAYS_ID_COL)
  private Day account;


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByteArray(this.file);
    dest.writeParcelable(this.account, flags);
  }

  public Attachment() {
  }

  protected Attachment(Parcel in) {
    this.file = in.createByteArray();
    this.account = in.readParcelable(Day.class.getClassLoader());
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
