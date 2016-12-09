package com.sayler.gina.interactor.days;

import com.sayler.gina.interactor.CommonInteractor;
import entity.Attachment;
import entity.Day;

import java.util.List;

public interface DiaryInteractor extends CommonInteractor {
  void loadAllData(DaysGetInteractorCallback interactorCallback);

  void loadDataById(long id, DaysGetInteractorCallback interactorCallback);

  void put(Day day, List<Attachment> attachments, DaysPutInteractorCallback interactorCallback);

  void delete(Day day, DaysDeleteInteractorCallback daysDeleteInteractorCallback);

  List<Day> getData();
}