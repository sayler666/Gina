/**
 * Created by sayler on 2016-11-22.
 * <p>

 */
package com.sayler.monia.domain.interactor;

public interface DaysPutInteractorCallback extends NoDatabaseCallback {
  void onDataPut();

  void onDataPutError(Throwable throwable);

}
