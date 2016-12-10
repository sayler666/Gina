package com.sayler.gina.dagger.module;

import com.sayler.gina.rx.IRxAndroidTransformer;
import com.sayler.gina.rx.RxAndroidTransformer;
import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

  @Provides
  public IRxAndroidTransformer provideIRxAndroidTransformer() {
    return new RxAndroidTransformer();
  }



}