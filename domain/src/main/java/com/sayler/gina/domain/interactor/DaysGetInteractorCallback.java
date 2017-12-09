/**
 * Created by sayler on 2016-11-22.
 * <p>

 */
package com.sayler.gina.domain.interactor;

public interface DaysGetInteractorCallback extends NoDatabaseCallback {
  void onDownloadData();

  void onDownloadDataError(Throwable throwable);
}
