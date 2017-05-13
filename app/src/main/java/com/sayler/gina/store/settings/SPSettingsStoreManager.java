package com.sayler.gina.store.settings;

import android.content.Context;
import com.sayler.gina.store.SharedPreferencesHelper;
import com.sayler.gina.util.Constants;

import javax.inject.Singleton;

@Singleton
public class SPSettingsStoreManager implements SettingsStoreManager {
  private final SharedPreferencesHelper<SettingsStore> sharedPreferencesHelper;

  public SPSettingsStoreManager(final Context context) {
    sharedPreferencesHelper = new SharedPreferencesHelper<>(context, Constants.PREF_SETTINGS_STORE, SettingsStore.class);
  }

  @Override
  public SettingsStore get() {
    return sharedPreferencesHelper.retrieve();
  }

  @Override
  public void save(SettingsStore object) {
    sharedPreferencesHelper.store(object);
  }

  @Override
  public void clear() {
    sharedPreferencesHelper.clear();
  }

}