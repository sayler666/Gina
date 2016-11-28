/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.interactor.days;

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
  private List<Day> data;

  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

  public DaysInteractorDb(IRxAndroidTransformer iRxAndroidTransformer, DaysDataProvider daysDataProvider) {
    this.iRxAndroidTransformer = iRxAndroidTransformer;
    this.daysDataProvider = daysDataProvider;
  }

  @Override
  public void loadAllData(DaysInteractorCallback interactorCallback) {
    this.interactorCallback = interactorCallback;
    retrieveAllData();
  }

  @Override
  public void loadDataById(long id, DaysInteractorCallback interactorCallback) {
    this.interactorCallback = interactorCallback;
    retrieveDataById(id);
  }

  @Override
  public List<Day> getData() {
    return data;
  }

  /* ------------------------------------------------------ PRIVATE ------------------------------------------------ */


  private void retrieveAllData() {
    Subscription subscription = null;
    try {
      subscription = rx.Observable.just(daysDataProvider.getAll())
          .compose(iRxAndroidTransformer.applySchedulers())
          .subscribe(this::handleLoadData, this::dispatchDefaultPresenterError);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    needToUnsubscribe(subscription);
  }

  private void retrieveDataById(long id) {
    Subscription subscription = null;
    try {
      subscription = rx.Observable.just(daysDataProvider.get(id))
          .compose(iRxAndroidTransformer.applySchedulers())
          .subscribe(this::handleLoadData, this::dispatchDefaultPresenterError);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    needToUnsubscribe(subscription);
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
    saveData(days);
    interactorCallback.onDownloadData();
  }

  private void saveData(List<Day> days) {
    data = days;
  }
}
