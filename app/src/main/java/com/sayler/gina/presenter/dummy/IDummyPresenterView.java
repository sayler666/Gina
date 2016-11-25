/**
 * Created by sayler on 2016-03-18.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.presenter.dummy;

import com.sayler.gina.presenter.IPresenterView;
import com.sayler.gina.model.Dummy;

import java.util.List;

/**
 * @author lchromy
 */
public interface IDummyPresenterView extends IPresenterView {
  void onServerError();

  void onDownloaded(List<Dummy> i);
}
