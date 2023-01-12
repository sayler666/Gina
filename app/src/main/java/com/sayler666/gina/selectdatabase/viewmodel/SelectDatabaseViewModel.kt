package com.sayler666.gina.selectdatabase.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.core.flow.Event
import com.sayler666.gina.core.flow.Event.Empty
import com.sayler666.gina.core.flow.Event.Value
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.destinations.Destination
import com.sayler666.gina.destinations.JournalScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectDatabaseViewModel @Inject constructor(
    private val databaseProvider: DatabaseProvider
) : ViewModel() {

    private val _permissionGranted = MutableStateFlow(Environment.isExternalStorageManager())
    val permissionGranted: StateFlow<Boolean>
        get() = _permissionGranted.asStateFlow()

    private val _navigateToHome = MutableStateFlow<Event<Destination>>(Empty)
    val navigateToHome: StateFlow<Event<Destination>>
        get() = _navigateToHome.filterNotNull()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Empty)

    fun refreshPermissionStatus() {
        _permissionGranted.value = Environment.isExternalStorageManager()
    }

    fun openDatabase(path: String) {
        viewModelScope.launch {
            if (databaseProvider.openDB(path)) _navigateToHome.tryEmit(
                Value(JournalScreenDestination)
            )
        }
    }
}
