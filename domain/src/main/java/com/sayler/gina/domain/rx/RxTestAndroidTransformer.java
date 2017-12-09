package com.sayler.gina.domain.rx;


import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lchromy on 07.10.15.
 * <p>

 */
public class RxTestAndroidTransformer implements IRxAndroidTransformer {

  @Override
  public <T> ObservableTransformer<T,T> applySchedulers() {
    return observable ->
        observable.subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.trampoline());
  }
}
