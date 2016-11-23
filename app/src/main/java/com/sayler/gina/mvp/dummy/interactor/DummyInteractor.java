package com.sayler.gina.mvp.dummy.interactor;

import com.sayler.gina.mvp.dummy.model.Dummy;

import java.util.List;

public interface DummyInteractor {
  void downloadData(InteractorCallback interactorCallback);
  List<Dummy> getData();
}