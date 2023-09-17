package com.sayler666.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope


interface ViewModelSlice {
    var sliceScope: CoroutineScope

    fun ViewModel.initialize() {
        sliceScope = viewModelScope
        afterInit()
    }

    fun afterInit() {}
}
