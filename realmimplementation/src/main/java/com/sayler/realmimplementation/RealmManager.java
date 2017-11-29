/**
 * Created by sayler on 2016-12-09.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.realmimplementation;

import com.sayler.gina.domain.DataManager;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * TODO Add class description...
 *
 * @author sayler
 */
class RealmManager implements DataManager<Realm> {

  private String realmSourcePath;
  private String realmDirectory;
  private String realmFileName;
  private boolean needNewInstance = true;
  private Realm instance;

  public RealmManager() {

  }

  @Override
  public void setSourceFile(String sourceFilePath) {
    this.realmSourcePath = sourceFilePath;
    this.realmDirectory =
        FilenameUtils.getPath(sourceFilePath);
    this.realmFileName =
        FilenameUtils.getName(sourceFilePath);
    needNewInstance = true;
  }

  @Override
  public boolean isOpen() {
    return realmDirectory != null;
  }

  @Override
  public void close() {
    if (instance != null && !instance.isClosed()) {
      instance.close();
    }
  }

  @Override
  public Realm getDao() {
    if (instance == null || needNewInstance || instance.isClosed()) {
      if (instance != null && !instance.isClosed()) {
        instance.close();
      }
      RealmConfiguration config = new RealmConfiguration.Builder()
          .directory(new File(realmDirectory))
          .name(realmFileName)
          .build();
      instance = Realm.getInstance(config);
      needNewInstance = false;
    }
    return instance;
  }

  @NotNull
  @Override
  public String getSourceFilePath() {
    return realmSourcePath;
  }
}
