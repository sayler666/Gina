package com.sayler.gina.store;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.sayler.gina.util.Constants;

import java.lang.reflect.Type;

/**
 * Helper for storing data in sp as String.
 * <p>
 * Created by miquido on 07/03/16.
 */
public class SharedPreferencesHelper<T> {

  protected String key;
  protected final Gson gson;
  protected final SharedPreferences sharedPreferences;
  protected Class<T> tClazz;
  protected Type listType;
  protected boolean storesList;

  public SharedPreferencesHelper(final Context context, final String key, final Class<T> tClazz) {
    storesList = false;
    this.tClazz = tClazz;
    this.gson = new Gson();
    this.key = key;
    sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
  }

  public SharedPreferencesHelper(final Context context, final String key, final Type listType) {
    storesList = true;
    this.listType = listType;
    this.gson = new Gson();
    this.key = key;
    sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
  }

  public void store(final T object) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(key, gson.toJson(object)).apply();
  }

  @SuppressLint("CommitPrefEdits")
  public void storeSync(final T object) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(key, gson.toJson(object)).commit();
  }

  public T retrieve() {
    String authStoreString = sharedPreferences.getString(key, "");
    if (TextUtils.isEmpty(authStoreString)) {
      return null;
    }
    return storesList ? gson.fromJson(authStoreString, listType) : gson.fromJson(authStoreString, tClazz);
  }

  public void clear() {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.remove(key).apply();
  }
}
