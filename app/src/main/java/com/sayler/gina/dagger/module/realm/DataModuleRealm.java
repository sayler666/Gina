package com.sayler.gina.dagger.module.realm;

import android.content.Context;
import com.sayler.gina.domain.DataModule;
import com.sayler.gina.domain.DataManager;
import com.sayler.gina.domain.realm.RealmManager;
import com.sayler.gina.domain.interactor.DiaryInteractor;
import com.sayler.gina.domain.ObjectCreator;
import com.sayler.gina.interactor.days.realm.DiaryInteractorRealm;
import com.sayler.gina.interactor.days.realm.ObjectCreatorRealm;
import com.sayler.gina.domain.presenter.diary.DiaryPresenter;
import com.sayler.gina.domain.rx.IRxAndroidTransformer;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DataModuleRealm extends DataModule {

  @Singleton
  @Provides
  DataManager provideDataManagerRealm() {
    //no default path
    return new RealmManager();
  }

  @Singleton
  @Provides
  ObjectCreator provideDayCreator() {
    return new ObjectCreatorRealm();
  }

  @SuppressWarnings("unchecked")
  @Provides
  DiaryInteractor provideDaysInteractorRealm(IRxAndroidTransformer iRxAndroidTransformer, DataManager dataManager) {

    return new DiaryInteractorRealm(iRxAndroidTransformer, dataManager);
  }

  @Provides
  DiaryPresenter provideDaysPresenter(Context context, DiaryInteractor diaryInteractorRealm) {
    return new DiaryPresenter(context, diaryInteractorRealm);
  }

}