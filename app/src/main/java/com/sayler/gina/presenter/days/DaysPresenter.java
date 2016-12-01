package com.sayler.gina.presenter.days;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.sayler.domain.dao.DBHelper;
import com.sayler.gina.interactor.days.DaysInteractor;
import com.sayler.gina.interactor.days.DaysInteractorCallback;
import com.sayler.gina.presenter.Presenter;
import entity.Day;

import java.util.List;

public class DaysPresenter extends Presenter<DaysPresenterView> {

  private static final String TAG = "DummyPresenter";

  private DaysInteractor daysInteractor;

  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

  public DaysPresenter(final Context context, final DaysInteractor daysInteractor) {
    this.daysInteractor = daysInteractor;
    needToFree(this.daysInteractor);
  }

  public void loadAll() {

    daysInteractor.loadAllData(new DaysInteractorCallback() {
      @Override
      public void onDownloadData() {
        List<Day> data = daysInteractor.getData();
        handleLoadData(data);
      }

      @Override
      public void onDownloadDataError(Throwable throwable) {
        dispatchDefaultPresenterError(throwable);
      }

      @Override
      public void onNoDatabase() {
        noDataSource();
      }
    });

  }

  public void loadById(long id) {

    daysInteractor.loadDataById(id, new DaysInteractorCallback() {
      @Override
      public void onDownloadData() {
        List<Day> data = daysInteractor.getData();
        handleLoadData(data);
      }

      @Override
      public void onDownloadDataError(Throwable throwable) {
        dispatchDefaultPresenterError(throwable);
      }

      @Override
      public void onNoDatabase() {
        noDataSource();
      }
    });

  }

  /* ------------------------------------------------------ PRIVATE ------------------------------------------------ */

  private void handleLoadData(List<Day> days) {
    if (presenterView != null) {
      presenterView.onDownloaded(days);
    }
  }

  private void noDataSource() {
    if (presenterView != null) {
      presenterView.onNoDataSource();
    }
  }

  private void dispatchDefaultPresenterError(Throwable throwable) {
    if (presenterView != null) {
      presenterView.onError(throwable.getMessage());
    }
    Log.e(TAG, throwable.getMessage(), throwable);
  }

}
