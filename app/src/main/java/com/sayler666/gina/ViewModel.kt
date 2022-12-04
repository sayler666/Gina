package com.sayler666.gina

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    data class Data(val number: Int)

    val dataFlow = flow {
        var i = 0
        while (true) {
            emit(Data(i++))
            delay(1000)
        }
    }.stateIn(
        viewModelScope,
        WhileSubscribed(5000),
        Data(0)
    )
}
