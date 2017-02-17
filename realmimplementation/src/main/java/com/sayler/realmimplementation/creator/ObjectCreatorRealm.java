/**
 * Created by sayler on 2016-12-10.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.realmimplementation.creator;

import com.sayler.gina.domain.IAttachment;
import com.sayler.gina.domain.IDay;
import com.sayler.realmimplementation.model.AttachmentRealm;
import com.sayler.realmimplementation.model.DayRealm;
import com.sayler.gina.domain.ObjectCreator;

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
