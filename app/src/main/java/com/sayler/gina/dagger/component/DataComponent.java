package com.sayler.gina.dagger.component;

import com.sayler.gina.activity.DayActivity;
import com.sayler.gina.activity.DayEditActivity;
import com.sayler.gina.activity.MainActivity;
import com.sayler.gina.dagger.module.ServicesModule;
import com.sayler.gina.dagger.module.realm.DataModuleRealm;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(
    modules = {
        DataModuleRealm.class,
        ServicesModule.class
    },
    dependencies = {
        ApplicationComponent.class
    }
)
public interface DataComponent {

  /* ---- ACTIVITY ---- */
  void inject(MainActivity mainActivity);

  void inject(DayActivity mainActivity);

  void inject(DayEditActivity mainActivity);


  /* ---- FRAGMENT ---- */


  /* ---- PRESENTER ---- */
}