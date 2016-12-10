package com.sayler.gina.interactor.days;

import com.sayler.domain.ormLite.entity.Attachment;
import com.sayler.gina.IDay;
import com.sayler.gina.interactor.CommonInteractor;

import java.util.List;

public interface DiaryInteractor extends CommonInteractor {
  void loadAllData(DaysGetInteractorCallback interactorCallback);

  void loadDataById(long id, DaysGetInteractorCallback interactorCallback);

  void put(IDay day, List<Attachment> attachments, DaysPutInteractorCallback interactorCallback);

  void delete(IDay day, DaysDeleteInteractorCallback daysDeleteInteractorCallback);

  List<IDay> getData();
}