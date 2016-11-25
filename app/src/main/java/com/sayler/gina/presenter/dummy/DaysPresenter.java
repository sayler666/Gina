package com.sayler.gina.presenter.dummy;

import android.content.Context;
import android.util.Log;
import com.sayler.gina.interactor.dummy.DaysInteractor;
import com.sayler.gina.interactor.dummy.DaysInteractorCallback;
import com.sayler.gina.presenter.Presenter;
import entity.Day;

import java.util.List;

public class DaysPresenter extends Presenter<DaysPresenterView> {

  private static final String TAG = "DummyPresenter";

  private DaysInteractor daysInteractor;

  public DaysPresenter(final Context context, final DaysInteractor daysInteractor) {
    this.daysInteractor = daysInteractor;
  }

  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

  public void download() {

    daysInteractor.downloadData(new DaysInteractorCallback() {
      @Override
      public void onDownloadData() {
        List<Day> data = daysInteractor.getData();
        handleComponentsInfo(data);
      }

      @Override
      public void onDownloadDataError(Throwable throwable) {
        dispatchDefaultPresenterError(throwable);
      }
    });

  }

  /* ------------------------------------------------------ HANDLERS ------------------------------------------------ */

  private void handleComponentsInfo(List<Day> s) {
    if (presenterView != null) {
      presenterView.onDownloaded(s);
    }
  }

  private void dispatchDefaultPresenterError(Throwable throwable) {
    if (presenterView != null) {
      presenterView.onServerError();
    }
    Log.e(TAG, throwable.getMessage(), throwable);
  }

  @Override
  public void onUnBindView() {
    super.onUnBindView();
    daysInteractor.freeResources();
  }
}
