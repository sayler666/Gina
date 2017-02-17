/**
 * Created by sayler on 2016-12-10.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.domain;

/**
 * @author sayler
 */
public abstract class ObjectCreator {
  public abstract IDay createDay();

  public abstract IAttachment createAttachment();
}
