/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.dagger.module;

import android.content.Context;
import com.sayler.gina.mvp.dummy.DummyPresenter;
import com.sayler.gina.mvp.dummy.interactor.DummyInteractor;
import com.sayler.gina.mvp.dummy.interactor.DummyInteractorDb;
import com.sayler.gina.rx.IRxAndroidTransformer;
import com.sayler.gina.rx.RxAndroidTransformer;
import dagger.Module;
import dagger.Provides;

@Module
public class ServicesModule {
  @Provides
  public IRxAndroidTransformer provideIRxAndroidTransformer() {
    return new RxAndroidTransformer();
  }

  @Provides
  public DummyInteractor provideDummyInteractor(IRxAndroidTransformer iRxAndroidTransformer) {
    return new DummyInteractorDb(iRxAndroidTransformer);
  }

  @Provides
  public DummyPresenter provideDummyPresenter(Context context, DummyInteractor dummyInteractor) {
    return new DummyPresenter(context, dummyInteractor);
  }
}
