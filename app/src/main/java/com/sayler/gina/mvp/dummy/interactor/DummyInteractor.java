package com.sayler.gina.mvp.dummy.interactor;

import java.util.List;

public interface DummyInteractor {
  void downloadData(InteractorCallback interactorCallback);
  List<String> getData();
}