package com.sayler.gina.presenter.diary;

import android.content.Context;
import android.util.Log;
import com.sayler.gina.IAttachment;
import com.sayler.gina.IDay;
import com.sayler.gina.interactor.days.DaysDeleteInteractorCallback;
import com.sayler.gina.interactor.days.DaysGetInteractorCallback;
import com.sayler.gina.interactor.days.DaysPutInteractorCallback;
import com.sayler.gina.interactor.days.DiaryInteractor;
import com.sayler.gina.presenter.Presenter;

import java.util.List;

public class DiaryPresenter extends Presenter<DiaryPresenterView> {

  private static final String TAG = "DummyPresenter";

  private DiaryInteractor diaryInteractor;

  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

  public DiaryPresenter(final Context context, final DiaryInteractor diaryInteractor) {
    this.diaryInteractor = diaryInteractor;
    needToFree(this.diaryInteractor);
  }

  public void loadAll() {

    diaryInteractor.loadAllData(new DaysGetInteractorCallback() {
      @Override
      public void onDownloadData() {
        List<IDay> data = diaryInteractor.getData();
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

    diaryInteractor.loadDataById(id, new DaysGetInteractorCallback() {
      @Override
      public void onDownloadData() {
        List<IDay> data = diaryInteractor.getData();
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

  public void put(IDay day, List<IAttachment> attachments) {

    diaryInteractor.put(day, attachments, new DaysPutInteractorCallback() {
      @Override
      public void onDataPut() {
        handleDataPut();
      }

      @Override
      public void onDataPutError(Throwable throwable) {
        dispatchDefaultPresenterError(throwable);
      }

      @Override
      public void onNoDatabase() {
        noDataSource();
      }
    });

  }

  public void delete(IDay day) {

    diaryInteractor.delete(day, new DaysDeleteInteractorCallback() {
      @Override
      public void onDataDelete() {
        handleDataDelete();
      }

      @Override
      public void onDataDeleteError(Throwable throwable) {
        dispatchDefaultPresenterError(throwable);
      }

      @Override
      public void onNoDatabase() {
        noDataSource();
      }
    });

  }

  /* ------------------------------------------------------ PRIVATE ------------------------------------------------ */

  private void handleDataDelete() {
    if (presenterView != null) {
      presenterView.onDelete();
    }
  }

  private void handleDataPut() {
    if (presenterView != null) {
      presenterView.onPut();
    }
  }

  private void handleLoadData(List<IDay> days) {
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
