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

  private DaysInteractorCallback interactorCallback;
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
  public void loadAllData(DaysInteractorCallback interactorCallback) {
    this.interactorCallback = interactorCallback;
    if (checkIfDbExist()) {
      retrieveAllData();
    }
  }

  @Override
  public void loadDataById(long id, DaysInteractorCallback interactorCallback) {
    this.interactorCallback = interactorCallback;
    if (checkIfDbExist()) {
      retrieveDataById(id);
    }
  }

  @Override
  public List<Day> getData() {
    return data;
  }

  /* ------------------------------------------------------ PRIVATE ------------------------------------------------ */

  private boolean checkIfDbExist() {
    if (!dbManager.ifDatabaseFileExists()) {
      this.interactorCallback.onNoDatabase();
      return false;
    }
    return true;
  }

  private void retrieveAllData() {
    Subscription subscription;
    try {
      subscription = rx.Observable.just(daysDataProvider.getAll())
          .compose(iRxAndroidTransformer.applySchedulers())
          .subscribe(this::handleLoadData, this::dispatchDefaultPresenterError);
      needToUnsubscribe(subscription);
    } catch (SQLException e) {
      e.printStackTrace();
      dispatchDefaultPresenterError(e);
    }
  }

  private void retrieveDataById(long id) {
    Subscription subscription;
    try {
      subscription = rx.Observable.just(daysDataProvider.get(id))
          .compose(iRxAndroidTransformer.applySchedulers())
          .subscribe(this::handleLoadData, this::dispatchDefaultPresenterError);
      needToUnsubscribe(subscription);
    } catch (SQLException e) {
      e.printStackTrace();
      dispatchDefaultPresenterError(e);
    }
  }

  private void handleLoadData(Day day) {
    List<Day> days = Collections.singletonList(day);
    saveData(days);
    interactorCallback.onDownloadData();
  }

  private void dispatchDefaultPresenterError(Throwable throwable) {
    interactorCallback.onDownloadDataError(throwable);
  }

  private void handleLoadData(List<Day> days) {
    Collections.sort(days);
    Collections.reverse(days);
    saveData(days);
    interactorCallback.onDownloadData();
  }

  private void saveData(List<Day> days) {
    data = days;
  }
}
