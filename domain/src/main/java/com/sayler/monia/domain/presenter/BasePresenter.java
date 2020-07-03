package com.sayler.monia.domain.presenter;

/**
 * Created by sayler on 03.08.2017.
 */

public interface BasePresenter<T> {
  void bindView(T iPresenterView);

  void unbindView();
}
