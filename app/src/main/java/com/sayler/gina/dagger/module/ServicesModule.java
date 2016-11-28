/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.dagger.module;

import android.content.Context;
import com.sayler.domain.dao.DaysDataProvider;
import com.sayler.gina.interactor.days.DaysInteractor;
import com.sayler.gina.interactor.days.DaysInteractorDb;
import com.sayler.gina.presenter.days.DaysPresenter;
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
  public DaysInteractor provideDummyInteractor(IRxAndroidTransformer iRxAndroidTransformer, DaysDataProvider daysDataProvider) {
    return new DaysInteractorDb(iRxAndroidTransformer, daysDataProvider);
  }

  @Provides
  public DaysPresenter provideDummyPresenter(Context context, DaysInteractor DaysInteractor) {
    return new DaysPresenter(context, DaysInteractor);
  }
}
