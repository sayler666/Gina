package com.sayler.gina.mvp;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class Presenter<T> {
  protected T presenterView;

  protected CompositeSubscription subscriptionsToUnsubscribe = new CompositeSubscription();

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
    subscriptionsToUnsubscribe.clear();
  }

  protected final void unsubscribe(Subscription subscription) {
    subscriptionsToUnsubscribe.remove(subscription);
  }

  protected final void unsubscribeAll() {
    subscriptionsToUnsubscribe.clear();
  }

  protected final void needToUnsubscribe(Subscription subscription) {
    subscriptionsToUnsubscribe.add(subscription);
  }
}
