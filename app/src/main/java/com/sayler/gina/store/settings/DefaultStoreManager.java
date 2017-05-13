package com.sayler.gina.store.settings;

/**
 * Created by sayler on 2017-05-13.
 * <p>
 * Copyright 2017 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
interface DefaultStoreManager<T> {
  public T get();
  public void save(T object);
  public void clear();
}
