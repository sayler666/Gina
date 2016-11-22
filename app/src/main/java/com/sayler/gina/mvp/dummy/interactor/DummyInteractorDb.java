/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.mvp.dummy.interactor;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.sayler.gina.rx.IRxAndroidTransformer;
import rx.Subscription;

import java.util.List;

public class DummyInteractorDb implements DummyInteractor {

  private InteractorCallback interactorCallback;
  private IRxAndroidTransformer iRxAndroidTransformer;
  private List<String> data;

  public DummyInteractorDb(IRxAndroidTransformer iRxAndroidTransformer) {
    this.iRxAndroidTransformer = iRxAndroidTransformer;
  }

  @Override
  public void downloadData(InteractorCallback interactorCallback) {

    this.interactorCallback = interactorCallback;
    retrieveData();
  }

  private void retrieveData() {
    List<String> strings = Stream.range(0, 100).map(integer -> String.format("Dzien: %s", String.valueOf(integer))).collect(Collectors.toList());

    Subscription subscription = rx.Observable.just(strings)
        .compose(iRxAndroidTransformer.applySchedulers())
        .subscribe(this::handleComponentsInfo, this::dispatchDefaultPresenterError);
  }

  private void dispatchDefaultPresenterError(Throwable throwable) {
    interactorCallback.onDownloadDataError(throwable);
  }

  private void handleComponentsInfo(List<String> s) {
    saveData(s);
    interactorCallback.onDownloadData();
  }

  private void saveData(List<String> s) {
    data = s;
  }

  @Override
  public List<String> getData() {
    return data;
  }
}
