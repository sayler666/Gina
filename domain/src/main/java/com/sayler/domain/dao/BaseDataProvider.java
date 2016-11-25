/**
 * Created by Lukasz Chromy on 10.01.14.
 * <p>
 * Copyright 2014 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.domain.dao;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.j256.ormlite.dao.Dao;
import entity.BaseEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Lukasz Chromy
 */
public abstract class BaseDataProvider<T extends BaseEntity> {

  private static final String TAG = "BaseDataProvider";
  private final String databasePat;
  protected Context context;
  protected Dao<T, Long> dao;
  private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
  private String databasePath;

  public BaseDataProvider(Context context, String databasePat) {
    this.databasePat = databasePat;
    rebind(context);
  }

  /**
   * rebind Context and setup Dao
   *
   * @param context
   */
  public void rebind(Context context) {
    this.context = context;
    dao = setupDao();
  }

  public String getDatabasePat() {
    return databasePat;
  }

  public Dao<T, Long> getDao() {
    return dao;
  }

  /**
   * Use DaoHelper.getDao(Class<T> brand);
   *
   * @return Dao<T, Long> dao - connection to database used to modify it's data
   */
  protected abstract Dao<T, Long> setupDao();

  public void notifyDataChanged() {
    MAIN_THREAD.post(new Runnable() {
      @Override
      public void run() {
        postOnMainThread();
      }
    });
  }

  public void postOnMainThread() {
  }

  public void deleteAll() {
    try {
      dao.delete(dao.queryForAll());
    } catch (SQLException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  public void delete(long id) {
    try {
      dao.delete(dao.queryForId(id));
    } catch (SQLException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  public void delete(T data) {
    try {
      dao.delete(data);
    } catch (SQLException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  public void delete(List<T> data) {
    try {
      dao.delete(data);
    } catch (SQLException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  public void saveAll(List<T> results) {
    final List<T> localResults = results;
    try {
      dao.callBatchTasks(new Callable<Void>() {

        @Override
        public Void call() throws Exception {
          for (T data : localResults) {
            save(data);
          }
          return null;
        }
      });
    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  public void save(T data) {
    try {
      dao.createOrUpdate(data);
    } catch (SQLException e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  public List<T> getAll() throws SQLException {
    return dao.queryForAll();
  }

  public T get(long id) throws SQLException {
    return dao.queryForId(id);
  }

  public int refresh(T data) {
    try {
      return dao.refresh(data);
    } catch (SQLException e) {
      Log.e(TAG, e.getMessage(), e);
    }
    return 0;
  }
}