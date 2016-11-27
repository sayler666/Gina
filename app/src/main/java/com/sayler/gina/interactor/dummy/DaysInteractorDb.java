/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.interactor.dummy;

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

  public DaysInteractorDb(IRxAndroidTransformer iRxAndroidTransformer, DaysDataProvider daysDataProvider) {
    this.iRxAndroidTransformer = iRxAndroidTransformer;
    this.daysDataProvider = daysDataProvider;
  }

  @Override
  public void downloadData(DaysInteractorCallback interactorCallback) {
    this.interactorCallback = interactorCallback;
    retrieveData();
  }

  @Override
  public List<Day> getData() {
    return data;
  }

  private void retrieveData() {

    Subscription subscription = null;
    try {
      subscription = rx.Observable.just(daysDataProvider.getAll())
          .compose(iRxAndroidTransformer.applySchedulers())
          .subscribe(this::handleComponentsInfo, this::dispatchDefaultPresenterError);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    needToUnsubscribe(subscription);
  }

  private void dispatchDefaultPresenterError(Throwable throwable) {
    interactorCallback.onDownloadDataError(throwable);
  }

  private void handleComponentsInfo(List<Day> s) {
    Collections.sort(s);
    saveData(s);
    interactorCallback.onDownloadData();
  }

  private void saveData(List<Day> s) {
    data = s;
  }
}
