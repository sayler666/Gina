/**
 * Created by sayler666 on 2015-05-27.
 * <p>

 */
package com.sayler.ormliteimplementation;

import android.content.Context;
import android.util.Log;
import com.j256.ormlite.dao.Dao;
import com.sayler.ormliteimplementation.entity.Day;

import java.sql.SQLException;

/**
 * @author sayler666
 */
public class DaysDataProvider extends BaseDataProvider<Day> {

  private static final String TAG = DaysDataProvider.class.getSimpleName();

  public DaysDataProvider(Context context) {
    super(context);
  }

  @Override
  protected Dao<Day, Long> setupDao() {
    try {
      DaoHelper.setOpenHelper(context,
          DBHelper.class);
      return DaoHelper.getDao(Day.class);
    } catch (SQLException e) {
      Log.e(TAG, e.getMessage(), e);
    }
    return null;
  }

}
