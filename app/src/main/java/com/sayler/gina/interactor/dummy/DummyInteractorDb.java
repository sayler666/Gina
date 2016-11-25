/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.interactor.dummy;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.sayler.gina.interactor.BaseInteractor;
import com.sayler.gina.model.Dummy;
import com.sayler.gina.rx.IRxAndroidTransformer;
import rx.Subscription;

import java.util.List;

public class DummyInteractorDb extends BaseInteractor implements DummyInteractor {

  private DummyInteractorCallback interactorCallback;
  private IRxAndroidTransformer iRxAndroidTransformer;
  private List<Dummy> data;

  public DummyInteractorDb(IRxAndroidTransformer iRxAndroidTransformer) {
    this.iRxAndroidTransformer = iRxAndroidTransformer;
  }

  @Override
  public void downloadData(DummyInteractorCallback interactorCallback) {
    this.interactorCallback = interactorCallback;
    retrieveData();
  }

  @Override
  public List<Dummy> getData() {
    return data;
  }

  private void retrieveData() {
    List<Dummy> strings = Stream.range(0, 10000).map(integer -> new Dummy("Dzien: " + integer, String.valueOf(2006 + integer))).collect(Collectors.toList());

    Subscription subscription = rx.Observable.just(strings)
        .compose(iRxAndroidTransformer.applySchedulers())
        .subscribe(this::handleComponentsInfo, this::dispatchDefaultPresenterError);
    needToUnsubscribe(subscription);
  }

  private void dispatchDefaultPresenterError(Throwable throwable) {
    interactorCallback.onDownloadDataError(throwable);
  }

  private void handleComponentsInfo(List<Dummy> s) {
    saveData(s);
    interactorCallback.onDownloadData();
  }

  private void saveData(List<Dummy> s) {
    data = s;
  }
}
