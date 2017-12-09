/**
 * Created by sayler on 2016-12-10.
 * <p>

 */
package com.sayler.ormliteimplementation.creator;

import com.sayler.gina.domain.IAttachment;
import com.sayler.gina.domain.IDay;
import com.sayler.gina.domain.ObjectCreator;
import com.sayler.ormliteimplementation.entity.Attachment;
import com.sayler.ormliteimplementation.entity.Day;

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

