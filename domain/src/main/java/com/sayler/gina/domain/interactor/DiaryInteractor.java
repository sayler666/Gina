package com.sayler.gina.domain.interactor;

import com.sayler.gina.domain.IAttachment;
import com.sayler.gina.domain.IDay;

import java.util.List;

public interface DiaryInteractor extends CommonInteractor {
  void loadAllData(DaysGetInteractorCallback interactorCallback);

  void loadDataById(long id, DaysGetInteractorCallback interactorCallback);

  void loadDataByTextContent(String searchText, DaysGetInteractorCallback interactorCallback);

  void put(IDay day, List<IAttachment> attachments, DaysPutInteractorCallback interactorCallback);

  void delete(IDay day, DaysDeleteInteractorCallback daysDeleteInteractorCallback);

  List<IDay> getData();
}