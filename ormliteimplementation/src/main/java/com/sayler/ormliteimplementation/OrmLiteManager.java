package com.sayler.ormliteimplementation;

import android.content.Context;
import com.annimon.stream.Stream;
import com.sayler.gina.domain.DataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miquido on 01/12/16.
 */

public class OrmLiteManager implements DataManager<List<BaseDataProvider>> {
  private List<BaseDataProvider> baseDataProviderList = new ArrayList<>();
  private Context context;

  public OrmLiteManager(Context context) {
    this.context = context;
  }

  public void add(BaseDataProvider baseDataProvider) {
    baseDataProviderList.add(baseDataProvider);
  }

  @Override
  public void setSourceFile(String sourceFilePath) {
    DBHelper.setDatabasePath(sourceFilePath);
    rebindProviders();
  }

  @Override
  public boolean isOpen() {
    return DBHelper.checkIfDatabaseFileExists();
  }

  @Override
  public void close() {
    //no used
  }

  @Override
  public List<BaseDataProvider> getDao() {
    return baseDataProviderList;
  }

  private void rebindProviders() {
    Stream.of(baseDataProviderList).forEach(dp -> dp.rebind(context));
  }
}
