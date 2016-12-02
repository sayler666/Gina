/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.interactor.days;

public interface DaysPutInteractorCallback extends NoDatabaseCallback {
  void onDataPut();

  void onDataPutError(Throwable throwable);

}
