package com.sayler.realmimplementation;

import com.sayler.gina.domain.DataManager;
import com.sayler.gina.domain.DataModule;
import com.sayler.gina.domain.ObjectCreator;
import com.sayler.gina.domain.interactor.DiaryInteractor;
import com.sayler.gina.domain.presenter.diary.DiaryContract;
import com.sayler.gina.domain.presenter.diary.DiaryPresenter;
import com.sayler.gina.domain.rx.IRxAndroidTransformer;
import com.sayler.realmimplementation.creator.ObjectCreatorRealm;
import com.sayler.realmimplementation.interactor.DiaryInteractorRealm;
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
  DiaryContract.Presenter provideDaysPresenter(DiaryInteractor diaryInteractorRealm) {
    return new DiaryPresenter(diaryInteractorRealm);
  }

}