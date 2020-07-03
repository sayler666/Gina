package com.sayler.monia.domain.rx;


import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lchromy on 07.10.15.
 * <p>

 */
public class RxAndroidTransformer implements IRxAndroidTransformer {
  /**
   * Observe on main thread, subscribe on IO thread
   *
   * @param <T>
   * @return
   */
  @Override
  public <T> ObservableTransformer<T,T> applySchedulers() {
    return observable ->
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
  }

}
