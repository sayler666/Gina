/**
 * Created by Lukasz Chromy on 10.01.14.
 *
 * Copyright 2014 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.domain.dao;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * @author Lukasz Chromy
 */
public class DaoHelper {
  private static DBHelper dbHelper = null;

  private DaoHelper() {
  }

  public static <T extends OrmLiteSqliteOpenHelper> void setOpenHelper(Context context, Class<T> type) {
    if (dbHelper == null) {
      dbHelper = (DBHelper) OpenHelperManager.getHelper(context, type);
    }

  }

  public static <T> Dao<T, Long> getDao(Class<T> type) throws SQLException {
    if (dbHelper != null) {
      return dbHelper.getDao(type);
    } else {
      return null;
    }
  }
}