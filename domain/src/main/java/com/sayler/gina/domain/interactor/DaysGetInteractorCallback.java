/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.domain.interactor;

public interface DaysGetInteractorCallback extends NoDatabaseCallback {
  void onDownloadData();

  void onDownloadDataError(Throwable throwable);
}