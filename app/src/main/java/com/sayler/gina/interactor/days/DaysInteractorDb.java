/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.interactor.days;

import com.sayler.domain.dao.DBManager;
import com.sayler.domain.dao.DaysDataProvider;
import com.sayler.gina.interactor.BaseInteractor;
import com.sayler.gina.rx.IRxAndroidTransformer;
import entity.Day;
import rx.Subscription;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class DaysInteractorDb extends BaseInteractor implements DaysInteractor {

  private DaysGetInteractorCallback daysGetInteractorCallback;
  private DaysPutInteractorCallback daysPutInteractorCallback;
  private IRxAndroidTransformer iRxAndroidTransformer;
  private DaysDataProvider daysDataProvider;
  private DBManager dbManager;
  private List<Day> data;

  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

  public DaysInteractorDb(IRxAndroidTransformer iRxAndroidTransformer, DaysDataProvider daysDataProvider, DBManager dbManager) {
    this.iRxAndroidTransformer = iRxAndroidTransformer;
    this.daysDataProvider = daysDataProvider;
    this.dbManager = dbManager;
  }

  @Override
  public void loadAllData(DaysGetInteractorCallback daysGetInteractorCallback) {
    this.daysGetInteractorCallback = daysGetInteractorCallback;
    if (checkIfDbExist(daysGetInteractorCallback)) {
      retrieveAllData();
    }
  }

  @Override
  public void loadDataById(long id, DaysGetInteractorCallback daysGetInteractorCallback) {
    this.daysGetInteractorCallback = daysGetInteractorCallback;
    if (checkIfDbExist(daysGetInteractorCallback)) {
      retrieveDataById(id);
    }
  }

  @Override
  public void put(Day day, DaysPutInteractorCallback daysPutInteractorCallback) {
    this.daysPutInteractorCallback = daysPutInteractorCallback;
    if (checkIfDbExist(daysPutInteractorCallback)) {
      putDataToDB(day);
    }
  }

  @Override
  public List<Day> getData() {
    return data;
  }

  /* ------------------------------------------------------ PRIVATE ------------------------------------------------ */

  private boolean checkIfDbExist(NoDatabaseCallback noDatabaseCallback) {
    if (!dbManager.ifDatabaseFileExists()) {
      noDatabaseCallback.onNoDatabase();
      return false;
    }
    return true;
  }

  private void putDataToDB(Day day) {
    try {
      daysDataProvider.save(day);
      daysPutInteractorCallback.onDataPut();
    } catch (SQLException e) {
      e.printStackTrace();
      daysPutInteractorCallback.onDataPutError(e);
    }
  }

  private void retrieveAllData() {
    Subscription subscription;
    try {
      subscription = rx.Observable.just(daysDataProvider.getAll())
          .compose(iRxAndroidTransformer.applySchedulers())
          .subscribe(this::handleLoadData, throwable ->
              daysGetInteractorCallback.onDownloadDataError(throwable));
      needToUnsubscribe(subscription);
    } catch (SQLException e) {
      e.printStackTrace();
      daysGetInteractorCallback.onDownloadDataError(e);
    }
  }

  private void retrieveDataById(long id) {
    Subscription subscription;
    try {
      subscription = rx.Observable.just(daysDataProvider.get(id))
          .compose(iRxAndroidTransformer.applySchedulers())
          .subscribe(this::handleLoadData, throwable ->
              daysGetInteractorCallback.onDownloadDataError(throwable));
      needToUnsubscribe(subscription);
    } catch (SQLException e) {
      e.printStackTrace();
      daysGetInteractorCallback.onDownloadDataError(e);
    }
  }

  private void handleLoadData(Day day) {
    List<Day> days = Collections.singletonList(day);
    saveData(days);
    daysGetInteractorCallback.onDownloadData();
  }

  private void handleLoadData(List<Day> days) {
    Collections.sort(days);
    Collections.reverse(days);
    saveData(days);
    daysGetInteractorCallback.onDownloadData();
  }

  private void saveData(List<Day> days) {
    data = days;
  }
}
