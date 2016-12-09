/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.dagger.module;

import android.content.Context;
import com.sayler.domain.dao.AttachmentsDataProvider;
import com.sayler.domain.dao.DBManager;
import com.sayler.domain.dao.DaysDataProvider;
import com.sayler.gina.interactor.days.DiaryInteractor;
import com.sayler.gina.interactor.days.DiaryInteractorDb;
import com.sayler.gina.presenter.days.DiaryPresenter;
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
  public DiaryInteractor provideDaysInteractor(IRxAndroidTransformer iRxAndroidTransformer, DaysDataProvider daysDataProvider, AttachmentsDataProvider attachmentsDataProvider, DBManager dbManager) {
    return new DiaryInteractorDb(iRxAndroidTransformer, daysDataProvider, attachmentsDataProvider, dbManager);
  }

  @Provides
  public DiaryPresenter provideDaysPresenter(Context context, DiaryInteractor DiaryInteractor) {
    return new DiaryPresenter(context, DiaryInteractor);
  }
}
