/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.interactor.days;

import com.sayler.domain.dao.AttachmentsDataProvider;
import com.sayler.domain.dao.DBManager;
import com.sayler.domain.dao.DaysDataProvider;
import com.sayler.gina.interactor.BaseInteractor;
import com.sayler.gina.rx.IRxAndroidTransformer;
import entity.Attachment;
import entity.Day;
import entity.IDay;
import rx.Subscription;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiaryInteractorDb extends BaseInteractor implements DiaryInteractor {

  private DaysGetInteractorCallback daysGetInteractorCallback;
  private DaysPutInteractorCallback daysPutInteractorCallback;
  private DaysDeleteInteractorCallback daysDeleteInteractorCallback;
  private IRxAndroidTransformer iRxAndroidTransformer;
  private DaysDataProvider daysDataProvider;
  private AttachmentsDataProvider attachmentsDataProvider;
  private DBManager dbManager;
  private List<Day> data;

  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

  public DiaryInteractorDb(IRxAndroidTransformer iRxAndroidTransformer, DaysDataProvider daysDataProvider, AttachmentsDataProvider attachmentsDataProvider, DBManager dbManager) {
    this.iRxAndroidTransformer = iRxAndroidTransformer;
    this.daysDataProvider = daysDataProvider;
    this.attachmentsDataProvider = attachmentsDataProvider;
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
  public void put(IDay day, List<Attachment> attachments, DaysPutInteractorCallback daysPutInteractorCallback) {
    this.daysPutInteractorCallback = daysPutInteractorCallback;
    if (checkIfDbExist(daysPutInteractorCallback)) {
      putDataToDB((Day) day, attachments);
    }
  }

  @Override
  public void delete(IDay day, DaysDeleteInteractorCallback daysDeleteInteractorCallback) {
    this.daysDeleteInteractorCallback = daysDeleteInteractorCallback;
    if (checkIfDbExist(daysPutInteractorCallback)) {
      deleteDay((Day) day);
    }
  }

  @Override
  public List<IDay> getData() {
    List<IDay> iDays = new ArrayList<>();
    for (Day day : data) {
      iDays.add(day);
    }
    return iDays;
  }

  /* ------------------------------------------------------ PRIVATE ------------------------------------------------ */

  private boolean checkIfDbExist(NoDatabaseCallback noDatabaseCallback) {
    if (!dbManager.ifDatabaseFileExists()) {
      noDatabaseCallback.onNoDatabase();
      return false;
    }
    return true;
  }

  private void deleteDay(Day day) {
    try {
      daysDataProvider.delete(day);
      daysDeleteInteractorCallback.onDataDelete();
    } catch (SQLException e) {
      e.printStackTrace();
      daysDeleteInteractorCallback.onDataDeleteError(e);
    }
  }

  private void putDataToDB(Day day, List<Attachment> attachments) {
    try {
      daysDataProvider.save(day);
      daysDataProvider.refresh(day);

      //add new attachments
      for (Attachment attachment : attachments) {
        attachment.setDay(day);
        attachmentsDataProvider.save(attachment);
      }

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
