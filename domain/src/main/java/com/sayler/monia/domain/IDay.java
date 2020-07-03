/**
 * Created by sayler on 2016-12-09.
 * <p>

 */
package com.sayler.monia.domain;

import org.joda.time.DateTime;

import java.util.Collection;

public interface IDay {
  long getId();

  void setId(long id);

  DateTime getDate();

  void setDate(DateTime date);

  String getContent();

  void setContent(String content);

  Collection<IAttachment> getAttachments();
}
