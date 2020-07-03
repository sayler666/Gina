/**
 * Created by sayler on 2016-11-25.
 * <p>

 */
package com.sayler.monia.domain.interactor;

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
