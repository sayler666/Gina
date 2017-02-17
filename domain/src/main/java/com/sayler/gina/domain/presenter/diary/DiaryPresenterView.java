/**
 * Created by sayler on 2016-03-18.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.domain.presenter.diary;

import com.sayler.gina.domain.IDay;
import com.sayler.gina.domain.presenter.CommonPresenterView;

import java.util.List;

/**
 * @author lchromy
 */
public interface DiaryPresenterView extends CommonPresenterView {
  void onError(String error);

  void onDownloaded(List<IDay> dayList);

  void onPut();

  void onNoDataSource();

  void onDelete();
}
