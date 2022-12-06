package com.sayler666.gina.days.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DaysViewModel @Inject constructor() : ViewModel() {

    private val _permissionGranted = MutableStateFlow(Environment.isExternalStorageManager())
    val permissionGranted: StateFlow<Boolean>
        get() = _permissionGranted.asStateFlow()

    fun refreshPermissionStatus() {
        _permissionGranted.value = Environment.isExternalStorageManager()
    }

    fun openDatabase(path: String) {
        // TODO
    }
}
