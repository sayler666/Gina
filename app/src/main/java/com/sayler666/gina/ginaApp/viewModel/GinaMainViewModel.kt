package com.sayler666.gina.ginaApp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.ginaApp.navigation.BottomNavigationVisibilityManager
import com.sayler666.gina.settings.SettingsStorage
import com.sayler666.gina.settings.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GinaMainViewModel @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    settings: SettingsStorage,
    bottomNavigationVisibilityManager: BottomNavigationVisibilityManager
) : ViewModel() {

    init {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
    }

    val hasRememberedDatabase: StateFlow<Boolean?> = settings.getDatabasePathFlow()
        .map { path -> path != null }
        .stateIn(
            viewModelScope,
            WhileSubscribed(5000),
            null
        )

    val theme: StateFlow<Theme> = settings.getThemeFlow().stateIn(
        viewModelScope,
        WhileSubscribed(5000),
        Theme.default()
    )

    val bottomBarState = bottomNavigationVisibilityManager.state
}
