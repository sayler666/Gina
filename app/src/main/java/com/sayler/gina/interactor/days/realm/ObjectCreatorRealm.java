/**
 * Created by sayler on 2016-12-10.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.interactor.days.realm;

import com.sayler.gina.IAttachment;
import com.sayler.gina.IDay;
import com.sayler.gina.interactor.days.ObjectCreator;
import realm.model.AttachmentRealm;
import realm.model.DayRealm;

/**
 * TODO Add class description...
 *
 * @author sayler
 */
public class ObjectCreatorRealm extends ObjectCreator {
  @Override
  public IDay createDay() {
    return new DayRealm();
  }

  @Override
  public IAttachment createAttachment() {
    return new AttachmentRealm();
  }
}
