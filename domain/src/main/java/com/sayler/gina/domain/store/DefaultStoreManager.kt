package com.sayler.gina.domain.store

/**
 * Created by sayler on 2017-05-13.
 *
 */
interface DefaultStoreManager<T> {
    fun get(): T?
    fun save(`object`: T)
    fun clear()
}
