package com.sayler.monia.domain.presenter;

import com.annimon.stream.Stream;
import com.sayler.monia.domain.interactor.CommonInteractor;

import java.util.ArrayList;
import java.util.List;

public abstract class Presenter<T> implements BasePresenter<T> {
  protected T presenterView;

  private List<CommonInteractor> interactorList = new ArrayList<>();

  @Override
  public void bindView(T iPresenterView) {
    presenterView = iPresenterView;
  }

  @Override
  public void unbindView() {
    freeResources();
  }

  private void freeResources() {
    presenterView = null;
    cleanupInteractors();
  }

  private void cleanupInteractors() {
    Stream.of(interactorList).forEach(CommonInteractor::freeResources);
  }

  protected final void needToFree(CommonInteractor commonInteractor) {
    interactorList.add(commonInteractor);
  }
}
