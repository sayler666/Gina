package realm.model;

/**
 * Created by lchromy on 26.05.15.
 */

import android.support.annotation.NonNull;
import entity.IDay;
import io.realm.RealmModel;
import io.realm.annotations.RealmClass;
import org.joda.time.DateTime;

@RealmClass
public class DayRealm implements RealmModel, Comparable<DayRealm>, IDay {
  private long date;
  private String content;

  public DayRealm() {
  }

  public DayRealm(long date) {
    this.date = date;
  }

  @Override
  public DateTime getDate() {
    return new DateTime(date);
  }

  @Override
  public void setDate(DateTime date) {
    this.date = date.getMillis();
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public int compareTo(@NonNull DayRealm dayRealm) {
    return Long.compare(date, dayRealm.getDate().getMillis());
  }
}
