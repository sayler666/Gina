package com.sayler.ormliteimplementation

import android.content.Context
import com.annimon.stream.Stream
import com.sayler.gina.domain.DataManager

import java.util.ArrayList

/**

 */

internal class OrmLiteManager(private val context: Context) : DataManager<@JvmWildcard List<BaseDataProvider<*>>> {
    private lateinit var sourceFilePath: String
    private val baseDataProviderList = ArrayList<BaseDataProvider<*>>()

    fun add(baseDataProvider: BaseDataProvider<*>) {
        baseDataProviderList.add(baseDataProvider)
        rebindProviders()
    }

    override fun setSourceFile(sourceFilePath: String) {
        this.sourceFilePath = sourceFilePath
        DBHelper.setDatabasePath(sourceFilePath)
        rebindProviders()
    }

    override fun getSourceFilePath(): String {
        return sourceFilePath
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
