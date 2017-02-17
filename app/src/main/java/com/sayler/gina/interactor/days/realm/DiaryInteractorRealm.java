/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.interactor.days.realm;

import com.annimon.stream.Stream;
import com.sayler.gina.domain.*;
import com.sayler.gina.domain.interactor.*;
import com.sayler.gina.domain.realm.model.AttachmentRealm;
import com.sayler.gina.domain.realm.model.DayRealm;
import com.sayler.gina.domain.rx.IRxAndroidTransformer;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiaryInteractorRealm extends BaseInteractor implements DiaryInteractor {

  private DaysGetInteractorCallback daysGetInteractorCallback;
  private DaysPutInteractorCallback daysPutInteractorCallback;
  private DaysDeleteInteractorCallback daysDeleteInteractorCallback;
  private IRxAndroidTransformer iRxAndroidTransformer;
  private DataManager<Realm> dataManager;
  private List<IDay> data = new ArrayList<>();

  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

  public DiaryInteractorRealm(IRxAndroidTransformer iRxAndroidTransformer, DataManager<Realm> dataManager) {
    this.iRxAndroidTransformer = iRxAndroidTransformer;
    this.dataManager = dataManager;
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
  public void loadDataByTextContent(String searchText, DaysGetInteractorCallback daysGetInteractorCallback) {
    this.daysGetInteractorCallback = daysGetInteractorCallback;
    if (checkIfDbExist(daysGetInteractorCallback)) {
      retrieveDataText(searchText);
    }
  }

  @Override
  public void put(IDay day, List<IAttachment> attachments, DaysPutInteractorCallback daysPutInteractorCallback) {
    this.daysPutInteractorCallback = daysPutInteractorCallback;
    if (checkIfDbExist(daysPutInteractorCallback)) {
      putDataToDB(day, attachments);
    }
  }

  @Override
  public void delete(IDay day, DaysDeleteInteractorCallback daysDeleteInteractorCallback) {
    this.daysDeleteInteractorCallback = daysDeleteInteractorCallback;
    if (checkIfDbExist(daysPutInteractorCallback)) {
      deleteDay(day);
    }
  }

  @Override
  public List<IDay> getData() {
    return data;
  }

  /* ------------------------------------------------------ PRIVATE ------------------------------------------------ */

  private boolean checkIfDbExist(NoDatabaseCallback noDatabaseCallback) {
    if (!dataManager.isOpen()) {
      noDatabaseCallback.onNoDatabase();
      return false;
    }
    return true;
  }

  private void deleteDay(IDay day) {
    try {
      Realm realm = dataManager.getDao();
      final RealmResults<DayRealm> results = realm.where(DayRealm.class).equalTo("id", day.getId()).findAll();
      realm.executeTransaction(realm1 -> results.deleteAllFromRealm());
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      daysDeleteInteractorCallback.onDataDeleteError(e);
    }
    daysDeleteInteractorCallback.onDataDelete();
  }

  private void putDataToDB(IDay day, List<IAttachment> attachments) {
    Realm realm = dataManager.getDao();
    //if empty id, means that we are storing new object in db
    if (day.getId() == -1) {
      day.setId(day.getDate().getMillis());
    }

    realm.beginTransaction();
    DayRealm dayRealm = realm.copyToRealmOrUpdate((DayRealm) day);
    dayRealm.getAttachments().deleteAllFromRealm();
    realm.commitTransaction();

    realm.beginTransaction();
    Stream.of(attachments)
        .map(iAttachment -> realm.copyToRealm(((AttachmentRealm) iAttachment)))
        .forEach(attachmentRealm ->
            dayRealm.getAttachments().add(attachmentRealm));
    realm.commitTransaction();
    daysPutInteractorCallback.onDataPut();
  }

  private void retrieveAllData() {
    Subscription subscription;
    RealmQuery<DayRealm> query = dataManager.getDao().where(DayRealm.class);
    subscription = rx.Observable.just(query.findAll())
        .compose(iRxAndroidTransformer.applySchedulers())
        .subscribe(this::handleLoadData, throwable ->
            daysGetInteractorCallback.onDownloadDataError(throwable));
    needToUnsubscribe(subscription);
  }

  private void retrieveDataById(long id) {
    Subscription subscription;
    RealmQuery<DayRealm> query = dataManager.getDao().where(DayRealm.class).equalTo("id", id);
    subscription = rx.Observable.just(query.findAll())
        .compose(iRxAndroidTransformer.applySchedulers())
        .subscribe(this::handleLoadData, throwable ->
            daysGetInteractorCallback.onDownloadDataError(throwable));
    needToUnsubscribe(subscription);
  }

  private void retrieveDataText(String searchText) {
    Subscription subscription;
    RealmQuery<DayRealm> query = dataManager.getDao().where(DayRealm.class).like("content", "*" + searchText + "*");
    subscription = rx.Observable.just(query.findAll())
        .compose(iRxAndroidTransformer.applySchedulers())
        .subscribe(this::handleLoadData, throwable ->
            daysGetInteractorCallback.onDownloadDataError(throwable));
    needToUnsubscribe(subscription);
  }

  private void handleLoadData(RealmResults<DayRealm> realmResults) {
    List<DayRealm> days = dataManager.getDao().copyFromRealm(realmResults);
    Collections.sort(days);
    Collections.reverse(days);
    saveData(days);
    daysGetInteractorCallback.onDownloadData();
  }

  private void saveData(List<DayRealm> days) {
    data = new ArrayList<>();
    for (DayRealm day : days) {
      data.add(day);
    }
  }
}
