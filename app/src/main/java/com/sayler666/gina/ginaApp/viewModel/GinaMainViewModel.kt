package com.sayler666.gina.ginaApp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.settings.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GinaMainViewModel @Inject constructor(
    settings: Settings
) : ViewModel() {
    val hasRememberedDatabase: StateFlow<Boolean?> = settings.getDatabasePathFlow()
        .map { path -> path != null }
        .stateIn(
            viewModelScope,
            WhileSubscribed(5000),
            null
        )
}
