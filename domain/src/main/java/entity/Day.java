package entity;

/**
 * Created by lchromy on 26.05.15.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "days")
public class Day extends BaseEntity {
  public static final String DATE_COL = "date";
  @DatabaseField(columnName = DATE_COL)
  private String date;
  public static final String CONTENT_COL = "content";
  @DatabaseField(columnName = CONTENT_COL)
  private String content;

  public Day() {
  }

  public Day(String date, String content) {
    this.date = date;
    this.content = content;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
