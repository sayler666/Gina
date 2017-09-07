package com.sayler.ormliteimplementation

import android.content.Context
import com.annimon.stream.Stream
import com.sayler.gina.domain.DataManager

import java.util.ArrayList

/**
 * Created by miquido on 01/12/16.
 */

internal class OrmLiteManager(private val context: Context) : DataManager<@JvmWildcard List<BaseDataProvider<*>>> {
    private val baseDataProviderList = ArrayList<BaseDataProvider<*>>()

    fun add(baseDataProvider: BaseDataProvider<*>) {
        baseDataProviderList.add(baseDataProvider)
    }

    override fun setSourceFile(sourceFilePath: String) {
        DBHelper.setDatabasePath(sourceFilePath)
        rebindProviders()
    }

    override val isOpen: Boolean
        get() = DBHelper.checkIfDatabaseFileExists()

    override fun close() {
        //no used
    }

    override val dao: List<BaseDataProvider<*>>
        get() = baseDataProviderList

    private fun rebindProviders() {
        baseDataProviderList.forEach { it.rebind(context) }
    }
}
