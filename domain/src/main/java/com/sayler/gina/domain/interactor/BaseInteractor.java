/**
 * Created by sayler on 2016-11-25.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.domain.interactor;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseInteractor {

  protected CompositeSubscription subscriptionsToUnsubscribe = new CompositeSubscription();

  public void freeResources() {
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
