/**
 * Created by Lukasz Chromy on 10.01.14.
 * <p>
 * Copyright 2014 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.domain.ormLite;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.j256.ormlite.dao.Dao;
import com.sayler.domain.ormLite.entity.BaseEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Lukasz Chromy
 */
public abstract class BaseDataProvider<T extends BaseEntity> {

  private static final String TAG = "BaseDataProvider";
  protected Context context;
  protected Dao<T, Long> dao;
  private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

  public BaseDataProvider(Context context) {
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

  public void deleteAll() throws SQLException {
    dao.delete(dao.queryForAll());
  }

  public void delete(long id) throws SQLException {
    dao.delete(dao.queryForId(id));
  }

  public void delete(T data) throws SQLException {
    dao.delete(data);
  }

  public void delete(List<T> data) throws SQLException {
    dao.delete(data);
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

  public void save(T data) throws SQLException {
    dao.createOrUpdate(data);
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