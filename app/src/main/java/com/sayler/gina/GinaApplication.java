package com.sayler.gina;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sayler.gina.activity.BaseActivity;
import com.sayler.gina.dagger.ComponentBuilder;
import com.sayler.gina.dagger.component.ApplicationComponent;
import com.sayler.gina.dagger.component.DataComponent;
import com.sayler.gina.image.ImageLoaderHelper;
import io.realm.Realm;

public class GinaApplication extends Application {

  private ApplicationComponent applicationComponent;
  private DataComponent dataComponent;

  @Override
  public void onCreate() {
    super.onCreate();

    Realm.init(this);

    createComponents();

    initImageLoader();
  }

  private void initImageLoader() {
    DisplayImageOptions options = new DisplayImageOptions.Builder()
        .cacheOnDisk(true)
        .build();

    ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(this)
        .tasksProcessingOrder(QueueProcessingType.LIFO)
        .defaultDisplayImageOptions(options);
    ImageLoaderHelper.getImageLoader().init(configBuilder.build());
  }

  public static DataComponent getDataComponentForActivity(BaseActivity baseActivity) {
    ((GinaApplication) baseActivity.getApplicationContext()).getApplicationComponent().inject(baseActivity);
    return ((GinaApplication) baseActivity.getApplicationContext()).getDataComponent();
  }

  public static DataComponent getDataComponent(Context context) {
    return ((GinaApplication) context.getApplicationContext()).getDataComponent();
  }

  private void createComponents() {
    applicationComponent = ComponentBuilder.createApplicationComponent(this);
    dataComponent = ComponentBuilder.createDataComponent(applicationComponent);
  }

  public DataComponent getDataComponent() {
    return dataComponent;
  }

  public ApplicationComponent getApplicationComponent() {
    return applicationComponent;
  }

  public static GinaApplication get(@NonNull Context context) {
    return (GinaApplication) context.getApplicationContext();
  }

}