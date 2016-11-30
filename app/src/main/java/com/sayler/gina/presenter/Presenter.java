package com.sayler.gina.presenter;

import com.annimon.stream.Stream;
import com.sayler.gina.interactor.CommonInteractor;

import java.util.ArrayList;
import java.util.List;

public abstract class Presenter<T> {
  protected T presenterView;

  protected List<CommonInteractor> interactorList = new ArrayList<>();

  public void onBindView(T iPresenterView) {
    presenterView = iPresenterView;
  }

  public void onUnBindView() {
    freeResources();
  }

  public T getPresenterView() {
    return presenterView;
  }

  private void freeResources() {
    presenterView = null;
    cleanupInteractors();
  }

  protected final void cleanupInteractors() {
    Stream.of(interactorList).forEach(CommonInteractor::freeResources);
  }

  protected final void needToFree(CommonInteractor commonInteractor) {
    interactorList.add(commonInteractor);
  }
}
