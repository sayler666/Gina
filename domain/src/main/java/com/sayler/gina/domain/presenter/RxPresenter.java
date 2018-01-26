package com.sayler.gina.domain.presenter;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class RxPresenter<T> implements BasePresenter<T> {
  protected T presenterView;

  private CompositeDisposable compositeDisposable = new CompositeDisposable();

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
    unsubscribeAll();
  }

  protected final void unsubscribeAll() {
    compositeDisposable.clear();
  }

  protected final void needToUnsubscribe(Disposable disposable) {
    compositeDisposable.add(disposable);
  }
}
