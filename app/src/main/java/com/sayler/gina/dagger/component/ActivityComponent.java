package com.sayler.gina.dagger.component;

import android.app.Activity;
import com.sayler.gina.dagger.module.ActivityModule;
import dagger.Component;

@Component(
    modules = {
        ActivityModule.class
    },
    dependencies = {
        ApplicationComponent.class
    }
)
public interface ActivityComponent {

  // Provide
  Activity activity();
}