/**
 * Created by sayler on 2016-12-10.
 *
 *

 */
package com.sayler.gina.domain

interface DataManager<T> {
    fun setSourceFile(sourceFilePath: String)

    val isOpen: Boolean

    fun getSourceFilePath(): String

    fun close()

    val dao: T
}
