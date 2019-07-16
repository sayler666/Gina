package com.sayler.gina.domain.interactor;

import com.sayler.gina.domain.IAttachment;
import com.sayler.gina.domain.IDay;
import org.joda.time.DateTime;

import java.util.List;

public interface DiaryInteractor extends CommonInteractor {

  void loadDataById(long id, DaysGetInteractorCallback interactorCallback);

  void loadDataNextAfterDate(DateTime dateTime, DaysGetNextPreviousInteractorCallback interactorCallback);

  void loadDataPreviousBeforeDate(DateTime dateTime, DaysGetNextPreviousInteractorCallback interactorCallback);

  void put(IDay day, List<IAttachment> attachments, DaysPutInteractorCallback interactorCallback);

  void delete(IDay day, DaysDeleteInteractorCallback daysDeleteInteractorCallback);

  List<IDay> getData();
}