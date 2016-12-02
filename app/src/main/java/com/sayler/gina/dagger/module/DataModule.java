package com.sayler.gina.dagger.module;

import android.content.Context;
import android.os.Environment;
import com.sayler.domain.dao.DBManager;
import com.sayler.domain.dao.DaysDataProvider;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DataModule {

  @Singleton
  @Provides
  public DBManager provideDbManager(Context context) {
    //TODO put this path somewhere
    String dbPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/db.sqlite";

    DBManager dbManager = new DBManager(context);
    dbManager.setDatabasePath(dbPath);
    return dbManager;
  }

  @Singleton
  @Provides
  public DaysDataProvider provideDaysDataProvider(Context context, DBManager dbManager) {
    DaysDataProvider daysDataProvider = new DaysDataProvider(context);
    dbManager.add(daysDataProvider);
    return daysDataProvider;
  }

}