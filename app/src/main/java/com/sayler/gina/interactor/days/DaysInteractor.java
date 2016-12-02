package com.sayler.gina.interactor.days;

import com.sayler.gina.interactor.CommonInteractor;
import entity.Day;

import java.util.List;

public interface DaysInteractor extends CommonInteractor {
  void loadAllData(DaysGetInteractorCallback interactorCallback);

  void loadDataById(long id, DaysGetInteractorCallback interactorCallback);

  void put(Day day, DaysPutInteractorCallback interactorCallback);

  List<Day> getData();
}