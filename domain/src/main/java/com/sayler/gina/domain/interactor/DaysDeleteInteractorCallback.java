/**
 * Created by sayler on 2016-12-02.
 * <p>

 */
package com.sayler.gina.domain.interactor;

public interface DaysDeleteInteractorCallback extends NoDatabaseCallback {
  void onDataDelete();

  void onDataDeleteError(Throwable throwable);

}