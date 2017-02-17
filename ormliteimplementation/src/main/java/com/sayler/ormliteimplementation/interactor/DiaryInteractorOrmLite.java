/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.ormliteimplementation.interactor;

import com.annimon.stream.Stream;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.sayler.gina.domain.DataManager;
import com.sayler.gina.domain.IAttachment;
import com.sayler.gina.domain.IDay;
import com.sayler.gina.domain.interactor.*;
import com.sayler.gina.domain.rx.IRxAndroidTransformer;
import com.sayler.ormliteimplementation.AttachmentsDataProvider;
import com.sayler.ormliteimplementation.DaysDataProvider;
import com.sayler.ormliteimplementation.entity.Attachment;
import com.sayler.ormliteimplementation.entity.Day;
import rx.Subscription;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiaryInteractorOrmLite extends BaseInteractor implements DiaryInteractor {

  private DaysGetInteractorCallback daysGetInteractorCallback;
  private DaysPutInteractorCallback daysPutInteractorCallback;
  private DaysDeleteInteractorCallback daysDeleteInteractorCallback;
  private IRxAndroidTransformer iRxAndroidTransformer;
  private DaysDataProvider daysDataProvider;
  private AttachmentsDataProvider attachmentsDataProvider;
  private DataManager ormLiteManager;
  private List<Day> data;

  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

  public DiaryInteractorOrmLite(IRxAndroidTransformer iRxAndroidTransformer, DaysDataProvider daysDataProvider, AttachmentsDataProvider attachmentsDataProvider, DataManager ormLiteManager) {
    this.iRxAndroidTransformer = iRxAndroidTransformer;
    this.daysDataProvider = daysDataProvider;
    this.attachmentsDataProvider = attachmentsDataProvider;
    this.ormLiteManager = ormLiteManager;
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
    if (!ormLiteManager.isOpen()) {
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

  private void putDataToDB(Day day, List<IAttachment> attachments) {
    try {
      daysDataProvider.save(day);
      daysDataProvider.refresh(day);

      //remove old
      DeleteBuilder<Attachment, Long> deleteBuilder = attachmentsDataProvider.getDao().deleteBuilder();
      deleteBuilder.where().eq(Attachment.DAYS_ID_COL, day.getId());
      deleteBuilder.delete();

      //set day field in attachment to create relation
      for (IAttachment attachment : attachments) {
        if (attachment.getFile().length > 2000000) {
          throw new SQLException("Length of file too big. File not saved in database!");
        }
        ((Attachment) attachment).setDay(day);
      }

      //store attachment in db
      Stream.of(attachments).map(iAttachment -> ((Attachment) iAttachment)).forEach(iAttachment -> {
        try {
          attachmentsDataProvider.save(iAttachment);
        } catch (SQLException e) {
          e.printStackTrace();
          daysPutInteractorCallback.onDataPutError(e);
        }
      });

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

  private void retrieveDataText(String string) {
    Subscription subscription;
    try {
      QueryBuilder<Day, Long> queryBuilder = daysDataProvider.getDao().queryBuilder();
      PreparedQuery<Day> preparedQuery = queryBuilder.where().like(Day.CONTENT_COL, "%" + string + "%").prepare();

      subscription = rx.Observable.just(daysDataProvider.getDao().query(preparedQuery))
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
