package com.sayler.gina.interactor.dummy;

import com.sayler.gina.interactor.CommonInteractor;
import com.sayler.gina.model.Dummy;

import java.util.List;

public interface DummyInteractor extends CommonInteractor {
  void downloadData(DummyInteractorCallback interactorCallback);
  List<Dummy> getData();
}