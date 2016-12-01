package com.sayler.domain.dao;

import android.content.Context;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miquido on 01/12/16.
 */

public class DBManager {
  private List<BaseDataProvider> baseDataProviderList = new ArrayList<>();
  private Context context;

  public DBManager(Context context) {
    this.context = context;
  }

  public void add(BaseDataProvider baseDataProvider) {
    baseDataProviderList.add(baseDataProvider);
  }

  public void rebindProviders() {
    Stream.of(baseDataProviderList).forEach(dp -> dp.rebind(context));
  }

  public void setDatabasePath(String databasePath) {
    DBHelper.setDatabasePath(databasePath);
  }

  public String getDatabasePath() {
    return DBHelper.getDatabasePath();
  }

  public boolean ifDatabaseFileExists() {
    return DBHelper.checkIfDatabaseFileExists();
  }
}
