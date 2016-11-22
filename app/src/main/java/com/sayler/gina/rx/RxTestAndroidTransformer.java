package com.sayler.gina.rx;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by lchromy on 07.10.15.
 * <p>
 * Copyright 2015 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
public class RxTestAndroidTransformer implements IRxAndroidTransformer {

  @Override
  public <T> Observable.Transformer<T, T> applySchedulers() {
    return observable ->
        observable.subscribeOn(Schedulers.immediate())
            .observeOn(Schedulers.immediate());
  }
}
