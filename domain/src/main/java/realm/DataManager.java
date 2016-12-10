/**
 * Created by sayler on 2016-12-10.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package realm;

public interface DataManager<T> {
  void setSourceFile(String sourceFilePath);

  boolean isOpen();

  void close();

  T getDao();
}
