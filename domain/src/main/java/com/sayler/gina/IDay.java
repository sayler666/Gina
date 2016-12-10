/**
 * Created by sayler on 2016-12-09.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina;

import org.joda.time.DateTime;

import java.util.Collection;

public interface IDay {
  long getId();

  void setId(long id);

  DateTime getDate();

  void setDate(DateTime date);

  String getContent();

  void setContent(String content);

  Collection<? extends IAttachment> getAttachments();
}
