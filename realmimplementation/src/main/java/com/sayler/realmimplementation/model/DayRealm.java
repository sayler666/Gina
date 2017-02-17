package com.sayler.realmimplementation.model;

/**
 * Created by lchromy on 26.05.15.
 */

import android.support.annotation.NonNull;
import com.sayler.gina.domain.IDay;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import org.joda.time.DateTime;

@RealmClass
public class DayRealm implements RealmModel, Comparable<DayRealm>, IDay {
  @PrimaryKey
  private long id = -1;
  private long date;
  private String content;
  private RealmList<AttachmentRealm> realmList;

  public DayRealm() {
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
    this.id = id;
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
  public RealmList<AttachmentRealm> getAttachments() {
    return realmList;
  }

  @Override
  public int compareTo(@NonNull DayRealm dayRealm) {
    return Long.compare(date, dayRealm.getDate().getMillis());
  }
}
