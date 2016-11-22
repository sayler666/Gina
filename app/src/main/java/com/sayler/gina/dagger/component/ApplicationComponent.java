package com.sayler.gina.dagger.component;

import android.content.Context;
import com.sayler.gina.activity.BaseActivity;
import com.sayler.gina.dagger.module.ApplicationModule;
import dagger.Component;

@Component(
    modules = {
        ApplicationModule.class
    }
)
public interface ApplicationComponent {

  Context context();

  // Injections

  void inject(BaseActivity mainActivity);
}