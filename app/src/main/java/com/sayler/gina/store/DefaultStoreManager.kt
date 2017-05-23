package com.sayler.gina.store

/**
 * Created by sayler on 2017-05-13.
 *
 *
 * Copyright 2017 MiQUiDO <http:></http:>//www.miquido.com/>. All rights reserved.
 */
interface DefaultStoreManager<T> {
    fun get(): T?
    fun save(`object`: T)
    fun clear()
}
