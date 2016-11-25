package com.sayler.gina.dagger.module;

import android.content.Context;
import android.os.Environment;
import com.sayler.domain.dao.DaysDataProvider;
import dagger.Module;
import dagger.Provides;

import java.io.File;

@Module
public class DataModule {

  @Provides
  public DaysDataProvider provideDaysDataProvider(Context context) {
    String dbPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    File file = new File(dbPath + "/db.sqlite");
    if (file.exists()) {
      return new DaysDataProvider(context, dbPath + "/db.sqlite");
    }
    return new DaysDataProvider(context, null);
  }

}