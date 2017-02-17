package com.sayler.gina.domain.rx;

import rx.Observable;

/**
 * Created by miquido on 10/11/16.
 */
public interface IRxAndroidTransformer {
  /**
   * Observe on main thread, subscribe on IO thread
   *
   * @param <T>
   * @return
   */

  <T> Observable.Transformer<T, T> applySchedulers();

}
