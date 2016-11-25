package com.sayler.gina.interactor.dummy;

import com.sayler.gina.interactor.CommonInteractor;
import entity.Day;

import java.util.List;

public interface DaysInteractor extends CommonInteractor {
  void downloadData(DaysInteractorCallback interactorCallback);
  List<Day> getData();
}