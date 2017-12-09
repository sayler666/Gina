/**
 * Created by sayler666 on 2015-05-27.
 * <p>

 */
package com.sayler.ormliteimplementation;

import android.content.Context;
import android.util.Log;
import com.j256.ormlite.dao.Dao;
import com.sayler.ormliteimplementation.entity.Attachment;

import java.sql.SQLException;

/**
 * @author sayler666
 */
public class AttachmentsDataProvider extends BaseDataProvider<Attachment> {

  private static final String TAG = AttachmentsDataProvider.class.getSimpleName();

  AttachmentsDataProvider(Context context) {
    super(context);
  }

  @Override
  protected Dao<Attachment, Long> setupDao() {
    try {
      DaoHelper.setOpenHelper(context,
          DBHelper.class);
      return DaoHelper.getDao(Attachment.class);
    } catch (SQLException e) {
      Log.e(TAG, e.getMessage(), e);
    }
    return null;
  }

}
