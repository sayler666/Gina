package com.sayler.gina.dagger;


import com.sayler.gina.GinaApplication;
import com.sayler.gina.dagger.component.ApplicationComponent;
import com.sayler.gina.dagger.component.DaggerApplicationComponent;
import com.sayler.gina.dagger.component.DaggerDataComponent;
import com.sayler.gina.dagger.component.DataComponent;
import com.sayler.gina.dagger.module.ApplicationModule;

public final class ComponentBuilder {
  public static ApplicationComponent createApplicationComponent(final GinaApplication application) {
    return DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(application))
        .build();
  }

  public static DataComponent createDataComponent(final ApplicationComponent applicationComponent) {
    return DaggerDataComponent.builder()
        .applicationComponent(applicationComponent)
        .build();
  }

  private ComponentBuilder() {
  }

}
