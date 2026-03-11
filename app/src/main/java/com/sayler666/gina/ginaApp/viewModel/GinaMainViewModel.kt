package com.sayler666.gina.ginaApp.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.navigation.BottomNavigationVisibilityManager
import com.sayler666.data.database.db.journal.GinaDatabaseProvider
import com.sayler666.gina.feature.settings.SettingsStorage
import com.sayler666.gina.navigation.Journal
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.navigation.SelectDatabase
import com.sayler666.gina.ui.theme.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
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

    private val _pendingDeepLink = MutableStateFlow<Route?>(null)
    val pendingDeepLink: StateFlow<Route?> = _pendingDeepLink.asStateFlow()

    // ViewModel-owned backStack: survives configuration changes.
    // Populated once when DB state is first known + deep link captured.
    val backStack: SnapshotStateList<Route> = mutableStateListOf()

    init {
        viewModelScope.launch { ginaDatabaseProvider.openSavedDB() }
        viewModelScope.launch {
            val hasDb = hasRememberedDatabase.filterNotNull().first()
            val startRoute = if (hasDb) Journal else SelectDatabase
            val deepLink = if (hasDb) _pendingDeepLink.value else null
            if (backStack.isEmpty()) {
                backStack.add(startRoute)
                deepLink?.let { backStack.add(it) }
            }
            if (deepLink != null) _pendingDeepLink.value = null
        }
    }

    fun setDeepLink(route: Route) {
        _pendingDeepLink.value = route
    }

    fun consumeDeepLink() {
        _pendingDeepLink.value = null
    }
}
