package com.sayler.gina.mvp.dummy;

import android.content.Context;
import com.sayler.gina.mvp.Presenter;
import com.sayler.gina.mvp.dummy.interactor.DummyInteractor;
import com.sayler.gina.mvp.dummy.interactor.InteractorCallback;
import com.sayler.gina.mvp.dummy.model.Dummy;

import java.util.List;

public class DummyPresenter extends Presenter<IDummyPresenterView> {

  public static final String TAG = "DummyPresenter";

  private Context context;
  private DummyInteractor dummyInteractor;

  public DummyPresenter(final Context context, final DummyInteractor dummyInteractor) {
    this.context = context;
    this.dummyInteractor = dummyInteractor;
  }

  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

  public void download() {

    dummyInteractor.downloadData(new InteractorCallback() {
      @Override
      public void onDownloadData() {
        List<Dummy> data = dummyInteractor.getData();
        handleComponentsInfo(data);
      }

      @Override
      public void onDownloadDataError(Throwable throwable) {
        dispatchDefaultPresenterError(throwable);
      }
    });

  }

  /* ------------------------------------------------------ HANDLERS ------------------------------------------------ */

  private void handleComponentsInfo(List<Dummy> s) {
    if (presenterView != null) {
      presenterView.onDownloaded(s);
    }
  }

  private void dispatchDefaultPresenterError(Throwable throwable) {
    if (presenterView != null) {
      presenterView.onServerError();
    }
  }

}
