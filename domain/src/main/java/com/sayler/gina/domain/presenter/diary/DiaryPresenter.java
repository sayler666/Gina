package com.sayler.gina.domain.presenter.diary;

import android.util.Log;
import com.sayler.gina.domain.IAttachment;
import com.sayler.gina.domain.IDay;
import com.sayler.gina.domain.interactor.*;
import com.sayler.gina.domain.presenter.Presenter;
import org.joda.time.DateTime;

import java.util.List;

public class DiaryPresenter extends Presenter<DiaryContract.View> implements DiaryContract.Presenter {

  private static final String TAG = "DummyPresenter";

  private DiaryInteractor diaryInteractor;

  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

  public DiaryPresenter(final DiaryInteractor diaryInteractor) {
    this.diaryInteractor = diaryInteractor;
    needToFree(this.diaryInteractor);
  }

  @Override
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

  @Override
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

  @Override
  public void loadNextAfterDate(DateTime dateTime) {
    diaryInteractor.loadDataNextAfterDate(dateTime, new DaysGetNextPreviousInteractorCallback() {
      @Override
      public void onDownloadData() {
        List<IDay> data = diaryInteractor.getData();
        handleLoadData(data);
      }

      @Override
      public void onNoNextItemAvailable() {
        if (presenterView != null) {
          presenterView.onError("No next item.");
        }
      }

      @Override
      public void onNoPreviousItemAvailable() {
        if (presenterView != null) {
          presenterView.onError("No previous item.");
        }
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

  @Override
  public void loadPreviousBeforeDate(DateTime dateTime) {
    diaryInteractor.loadDataPreviousBeforeDate(dateTime, new DaysGetNextPreviousInteractorCallback() {
      @Override
      public void onDownloadData() {
        List<IDay> data = diaryInteractor.getData();
        handleLoadData(data);
      }

      @Override
      public void onNoNextItemAvailable() {
        if (presenterView != null) {
          presenterView.onError("No next item.");
        }
      }

      @Override
      public void onNoPreviousItemAvailable() {
        if (presenterView != null) {
          presenterView.onError("No previous item.");
        }
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

  @Override
  public void loadByTextSearch(String searchText) {

    diaryInteractor.loadDataByTextContent(searchText, new DaysGetInteractorCallback() {
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

  @Override
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

  @Override
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
