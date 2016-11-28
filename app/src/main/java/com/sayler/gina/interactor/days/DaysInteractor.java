package com.sayler.gina.interactor.days;

import com.sayler.gina.interactor.CommonInteractor;
import entity.Day;

import java.util.List;

public interface DaysInteractor extends CommonInteractor {
  void loadAllData(DaysInteractorCallback interactorCallback);

  void loadDataById(long id, DaysInteractorCallback interactorCallback);

  List<Day> getData();
}