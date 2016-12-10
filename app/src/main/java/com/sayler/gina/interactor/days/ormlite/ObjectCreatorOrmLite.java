/**
 * Created by sayler on 2016-12-10.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.interactor.days.ormlite;

import com.sayler.domain.ormLite.entity.Attachment;
import com.sayler.domain.ormLite.entity.Day;
import com.sayler.gina.IAttachment;
import com.sayler.gina.IDay;
import com.sayler.gina.interactor.days.ObjectCreator;

/**
 * TODO Add class description...
 *
 * @author sayler
 */
public class ObjectCreatorOrmLite extends ObjectCreator {
  public IDay createDay() {
    return new Day();
  }

  @Override
  public IAttachment createAttachment() {
    return new Attachment();
  }

}

