package com.sayler.gina.dagger.module;

import android.content.Context;
import com.sayler.gina.GinaApplication;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

  private final GinaApplication application;

  public ApplicationModule(final GinaApplication application) {
    this.application = application;
  }

  @Provides
  public Context provideContext() {
    return application;
  }

}