package com.sayler.gina.dagger.module;

import android.app.Activity;
import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

  private final Activity activity;

  public ActivityModule(final Activity activity) {
    this.activity = activity;
  }

  @Provides
  public Activity provideActivity() {
    return activity;
  }
}