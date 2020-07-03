/**
 * Created by sayler on 2016-11-22.
 * <p>
 */
package com.sayler.monia.domain.interactor;

public interface DaysGetNextPreviousInteractorCallback extends DaysGetInteractorCallback {
  void onNoNextItemAvailable();

  void onNoPreviousItemAvailable();
}
