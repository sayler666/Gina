package com.sayler.gina.domain.rx;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lchromy on 07.10.15.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
public class RxAndroidTransformer implements IRxAndroidTransformer {
  /**
   * Observe on main thread, subscribe on IO thread
   *
   * @param <T>
   * @return
   */
  @Override
  public <T> Observable.Transformer<T, T> applySchedulers() {
    return observable ->
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
  }

}
