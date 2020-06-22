package com.sayler.gina3.entry

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sayler.gina3.data.IDataManager

class EntryViewModel @ViewModelInject constructor(
    private val dataManager: IDataManager
) : ViewModel() {

    val databaseOpened: LiveData<Boolean>
        get() = dataManager.dbOpen().asLiveData()

    fun openDb(path: String) {
        dataManager.setSourceFile(path)
    }

}
