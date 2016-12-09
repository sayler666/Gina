/**
 * Created by sayler on 2016-12-09.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package entity;

import org.joda.time.DateTime;

public interface IDay {
  DateTime getDate();

  void setDate(DateTime date);

  String getContent();

  void setContent(String content);
}
