package com.sayler666.gina.dayslist.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.dayslist.usecase.GetDaysUseCase
import com.sayler666.gina.db.DatabaseProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DaysViewModel @Inject constructor(
    private val databaseProvider: DatabaseProvider,
    getDaysUseCase: GetDaysUseCase
) : ViewModel() {

    init {
        databaseProvider.openSavedDB()
    }

    private val _permissionGranted = MutableStateFlow(Environment.isExternalStorageManager())
    val permissionGranted: StateFlow<Boolean>
        get() = _permissionGranted.asStateFlow()

    val days = getDaysUseCase.getDaysFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun refreshPermissionStatus() {
        _permissionGranted.value = Environment.isExternalStorageManager()
    }

    fun openDatabase(path: String) {
        databaseProvider.openDB(path).getOpenedDb()
    }
}
