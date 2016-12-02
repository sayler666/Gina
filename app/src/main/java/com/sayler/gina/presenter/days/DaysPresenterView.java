/**
 * Created by sayler on 2016-03-18.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.presenter.days;

import com.sayler.gina.presenter.CommonPresenterView;
import entity.Day;

import java.util.List;

/**
 * @author lchromy
 */
public interface DaysPresenterView extends CommonPresenterView {
  void onError(String error);

  void onDownloaded(List<Day> dayList);

  void onPut();

  void onNoDataSource();
}
