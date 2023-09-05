package com.sayler666.gina.selectdatabase.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.destinations.Destination
import com.sayler666.gina.destinations.JournalScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectDatabaseViewModel @Inject constructor(
    private val databaseProvider: DatabaseProvider
) : ViewModel() {

    private val _permissionGranted = MutableStateFlow(Environment.isExternalStorageManager())
    val permissionGranted: StateFlow<Boolean> = _permissionGranted

    private val _navigateToHome = MutableSharedFlow<Destination>()
    val navigateToHome = _navigateToHome.asSharedFlow()

    fun refreshPermissionStatus() {
        _permissionGranted.value = Environment.isExternalStorageManager()
    }

    fun openDatabase(path: String) {
        viewModelScope.launch {
            if (databaseProvider.openDB(path)) _navigateToHome.emit(JournalScreenDestination)
        }
    }
}
