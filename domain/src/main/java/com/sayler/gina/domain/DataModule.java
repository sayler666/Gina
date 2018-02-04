package com.sayler.gina.domain;

import com.sayler.gina.domain.presenter.list.usecase.CalculateStatisticsUseCase;
import com.sayler.gina.domain.rx.IRxAndroidTransformer;
import com.sayler.gina.domain.rx.RxAndroidTransformer;
import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

  @Provides
  public IRxAndroidTransformer provideIRxAndroidTransformer() {
    return new RxAndroidTransformer();
  }

  @Provides
  public CalculateStatisticsUseCase provideCalculateStatisticsUseCase() {
    return new CalculateStatisticsUseCase();
  }

}