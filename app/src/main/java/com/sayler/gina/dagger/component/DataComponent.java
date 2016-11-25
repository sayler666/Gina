package com.sayler.gina.dagger.component;

import com.sayler.gina.activity.MainActivity;
import com.sayler.gina.dagger.module.DataModule;
import com.sayler.gina.dagger.module.ServicesModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(
    modules = {
        ServicesModule.class,
        DataModule.class
    },
    dependencies = {
        ApplicationComponent.class
    }
)
public interface DataComponent {

  /* ---- ACTIVITY ---- */
  void inject(MainActivity mainActivity);


  /* ---- FRAGMENT ---- */


  /* ---- PRESENTER ---- */
}