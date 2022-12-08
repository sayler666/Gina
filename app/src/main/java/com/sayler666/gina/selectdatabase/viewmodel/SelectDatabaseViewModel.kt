package com.sayler666.gina.selectdatabase.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.destinations.DaysDestination
import com.sayler666.gina.destinations.SelectDatabaseScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SelectDatabaseViewModel @Inject constructor(
    private val databaseProvider: DatabaseProvider
) : ViewModel() {

    private var navigator: DestinationsNavigator? = null

    private val _permissionGranted = MutableStateFlow(Environment.isExternalStorageManager())
    val permissionGranted: StateFlow<Boolean>
        get() = _permissionGranted.asStateFlow()

    fun attachDestinationsNavigator(destinationsNavigator: DestinationsNavigator) {
        navigator = destinationsNavigator
        if (databaseProvider.openSavedDB()) navigateToDaysScreen()
    }

    fun refreshPermissionStatus() {
        _permissionGranted.value = Environment.isExternalStorageManager()
    }

    fun openDatabase(path: String) {
        if (databaseProvider.openDB(path)) navigateToDaysScreen()
    }

    private fun navigateToDaysScreen() {
        navigator?.navigate(DaysDestination, builder = {
            popUpTo(SelectDatabaseScreenDestination.route) { inclusive = true }
        })
    }
}
