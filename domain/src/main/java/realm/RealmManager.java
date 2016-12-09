/**
 * Created by sayler on 2016-12-09.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package realm;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * TODO Add class description...
 *
 * @author sayler
 */
public class RealmManager {

  private String realmDirectory;
  private String realmFileName;
  private boolean needNewInstance = true;
  private Realm instance;

  public RealmManager() {

  }

  public void setRealmFile(String filePath) {
    this.realmDirectory =
        FilenameUtils.getPath(filePath);
    this.realmFileName =
        FilenameUtils.getName(filePath);
    needNewInstance = true;
  }

  public boolean ifDatabaseFileExists() {
    return realmDirectory != null;
  }

  public Realm getRealm() {
    if (instance == null || needNewInstance) {
      RealmConfiguration config = new RealmConfiguration.Builder()
          .directory(new File(realmDirectory))
          .name(realmFileName)
          .build();
      instance = Realm.getInstance(config);
      needNewInstance = false;
    }
    return instance;
  }
}
