/**
 * Created by sayler on 2016-03-18.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.presenter.days;

import com.sayler.gina.presenter.IPresenterView;
import entity.Day;

import java.util.List;

/**
 * @author lchromy
 */
public interface DaysPresenterView extends IPresenterView {
  void onError();

  void onDownloaded(List<Day> i);
}
