package com.sayler.ormliteimplementation;

import android.content.Context;
import android.os.Environment;
import com.sayler.gina.domain.DataManager;
import com.sayler.gina.domain.DataModule;
import com.sayler.gina.domain.ObjectCreator;
import com.sayler.gina.domain.interactor.DiaryInteractor;
import com.sayler.gina.domain.presenter.diary.DiaryPresenter;
import com.sayler.gina.domain.rx.IRxAndroidTransformer;
import com.sayler.ormliteimplementation.creator.ObjectCreatorOrmLite;
import com.sayler.ormliteimplementation.interactor.DiaryInteractorOrmLite;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DataModuleOrmLite extends DataModule {

  @Singleton
  @Provides
  public DataManager provideDataManagerOrmLite(Context context) {
    //TODO put this path somewhere
    //default path
    String dbPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/db.sqlite";

    OrmLiteManager ormLiteManager = new OrmLiteManager(context);
    ormLiteManager.setSourceFile(dbPath);
    return ormLiteManager;
  }

  @Singleton
  @Provides
  public ObjectCreator provideDayCreator() {
    return new ObjectCreatorOrmLite();
  }

  @Singleton
  @Provides
  public DaysDataProvider provideDaysDataProvider(Context context, DataManager ormLiteManager) {
    DaysDataProvider daysDataProvider = new DaysDataProvider(context);
    ((OrmLiteManager) ormLiteManager).add(daysDataProvider);
    return daysDataProvider;
  }

  @Singleton
  @Provides
  public AttachmentsDataProvider provideAttachmentsDataProvider(Context context, DataManager ormLiteManager) {
    AttachmentsDataProvider attachmentsDataProvider = new AttachmentsDataProvider(context);
    ((OrmLiteManager) ormLiteManager).add(attachmentsDataProvider);
    return attachmentsDataProvider;
  }

  @Provides
  public DiaryInteractor provideDaysInteractorOrmLite(IRxAndroidTransformer iRxAndroidTransformer, DaysDataProvider daysDataProvider, AttachmentsDataProvider attachmentsDataProvider, DataManager dataManager) {
    return new DiaryInteractorOrmLite(iRxAndroidTransformer, daysDataProvider, attachmentsDataProvider, dataManager);
  }

  @Provides
  public DiaryPresenter provideDaysPresenter(Context context, DiaryInteractor diaryInteractor) {
    return new DiaryPresenter(context, diaryInteractor);
  }

}