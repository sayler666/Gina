package com.sayler.gina.domain.rx;

import io.reactivex.ObservableTransformer;
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

  <T> ObservableTransformer<T, T> applySchedulers();

}
