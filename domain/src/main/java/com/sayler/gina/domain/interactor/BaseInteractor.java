/**
 * Created by sayler on 2016-11-25.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.domain.interactor;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseInteractor {

  protected CompositeDisposable compositeDisposable = new CompositeDisposable();

  public void freeResources() {
    compositeDisposable.clear();
  }

  protected final void unsubscribe(Disposable disposable) {
    compositeDisposable.remove(disposable);
  }

  protected final void unsubscribeAll() {
    compositeDisposable.clear();
  }

  protected final void needToUnsubscribe(Disposable disposable) {
    compositeDisposable.add(disposable);
  }
}
