/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.interactor.days;

import com.sayler.gina.interactor.BaseInteractor;
import com.sayler.gina.rx.IRxAndroidTransformer;
import entity.Attachment;
import entity.IDay;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import realm.RealmManager;
import realm.model.DayRealm;
import rx.Subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiaryInteractorRealm extends BaseInteractor implements DiaryInteractor {

  private DaysGetInteractorCallback daysGetInteractorCallback;
  private DaysPutInteractorCallback daysPutInteractorCallback;
  private DaysDeleteInteractorCallback daysDeleteInteractorCallback;
  private IRxAndroidTransformer iRxAndroidTransformer;
  private RealmManager realmManager;
  private List<IDay> data = new ArrayList<>();

  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

  public DiaryInteractorRealm(IRxAndroidTransformer iRxAndroidTransformer, RealmManager realmManager) {
    this.iRxAndroidTransformer = iRxAndroidTransformer;
    this.realmManager = realmManager;
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
  public void put(IDay day, List<Attachment> attachments, DaysPutInteractorCallback daysPutInteractorCallback) {
    this.daysPutInteractorCallback = daysPutInteractorCallback;
    if (checkIfDbExist(daysPutInteractorCallback)) {
      putDataToDB(day, attachments);
    }
  }

  @Override
  public void delete(IDay day, DaysDeleteInteractorCallback daysDeleteInteractorCallback) {
//    this.daysDeleteInteractorCallback = daysDeleteInteractorCallback;
//    if (checkIfDbExist(daysPutInteractorCallback)) {
//      deleteDay(day);
//    }
  }

  @Override
  public List<IDay> getData() {
    return data;
  }

  /* ------------------------------------------------------ PRIVATE ------------------------------------------------ */

  private boolean checkIfDbExist(NoDatabaseCallback noDatabaseCallback) {
    if (!realmManager.ifDatabaseFileExists()) {
      noDatabaseCallback.onNoDatabase();
      return false;
    }
    return true;
  }

  private void deleteDay(IDay day) {
//    try {
//      daysDataProvider.delete(day);
//      daysDeleteInteractorCallback.onDataDelete();
//    } catch (SQLException e) {
//      e.printStackTrace();
//      daysDeleteInteractorCallback.onDataDeleteError(e);
//    }
  }

  private void putDataToDB(IDay day, List<Attachment> attachments) {
    Realm realm = realmManager.getRealm();
    realm.beginTransaction();
    realm.copyToRealm((DayRealm) day);
    realm.commitTransaction();
    daysPutInteractorCallback.onDataPut();
  }

  private void retrieveAllData() {
    Subscription subscription;
    RealmQuery<DayRealm> query = realmManager.getRealm().where(DayRealm.class);
    subscription = rx.Observable.just(query.findAll())
        .compose(iRxAndroidTransformer.applySchedulers())
        .subscribe(this::handleLoadData, throwable ->
            daysGetInteractorCallback.onDownloadDataError(throwable));
    needToUnsubscribe(subscription);
  }

  private void retrieveDataById(long id) {
//    Subscription subscription;
//      RealmQuery<DayRealm> query = realmManager.getRealm().where(DayRealm.class);
//      subscription = rx.Observable.just(query.find(id))
//          .compose(iRxAndroidTransformer.applySchedulers())
//          .subscribe(this::handleLoadData, throwable ->
//              daysGetInteractorCallback.onDownloadDataError(throwable));
//      needToUnsubscribe(subscription);

  }

  private void handleLoadData(IDay day) {
//    List<Day> days = Collections.singletonList(day);
//    saveData(days);
//    daysGetInteractorCallback.onDownloadData();
  }

  private void handleLoadData(RealmResults<DayRealm> realmResults) {
    List<DayRealm> days = realmManager.getRealm().copyFromRealm(realmResults);
    Collections.sort(days);
    Collections.reverse(days);
    saveData(days);
    daysGetInteractorCallback.onDownloadData();
  }

  private void saveData(List<DayRealm> days) {
    for (DayRealm day : days) {
      data.add(day);
    }
  }
}
