package com.sayler.monia.domain.rx;

import io.reactivex.ObservableTransformer;
/**

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
