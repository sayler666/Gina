package com.sayler666.gina.ginaApp.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.navigation.BottomBarState
import com.sayler666.core.navigation.BottomBarState.Shown
import com.sayler666.core.navigation.BottomNavigationVisibilityManager
import com.sayler666.gina.feature.settings.SettingsStorage
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel.ViewEvent.ConsumeDeepLink
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel.ViewEvent.SetDeepLink
import com.sayler666.gina.navigation.routes.Journal
import com.sayler666.gina.navigation.routes.Route
import com.sayler666.gina.navigation.routes.Startup
import com.sayler666.gina.ui.theme.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GinaMainViewModel @Inject constructor(
    settings: SettingsStorage,
    bottomNavigationVisibilityManager: BottomNavigationVisibilityManager
) : ViewModel() {

    private val mutableViewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = mutableViewState.asStateFlow()

    // ViewModel-owned backStack: survives configuration changes.
    val backStack: SnapshotStateList<Route> = mutableStateListOf()

    init {
        observeDatabasePath(settings)
        observeTheme(settings)
        observeBottomBarState(bottomNavigationVisibilityManager)
        initBackStack()
    }

    private fun observeDatabasePath(settings: SettingsStorage) {
        settings.getDatabasePathFlow()
            .map { path -> path != null }
            .onEach { hasDb ->
                mutableViewState.update { it.copy(databaseReady = hasDb) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeTheme(settings: SettingsStorage) {
        settings.getThemeFlow()
            .onEach { theme ->
                mutableViewState.update { it.copy(theme = theme) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeBottomBarState(manager: BottomNavigationVisibilityManager) {
        manager.state
            .onEach { state ->
                mutableViewState.update { it.copy(bottomBarState = state) }
            }
            .launchIn(viewModelScope)
    }

    private fun initBackStack() {
        viewModelScope.launch {
            val hasDb = viewState.map { it.databaseReady }.filterNotNull().first()
            val deepLink = if (hasDb) viewState.value.pendingDeepLink else null
            if (backStack.isEmpty()) {
                backStack.add(if (hasDb) Journal else Startup)
                deepLink?.let { backStack.add(it) }
            }
            if (deepLink != null) mutableViewState.update { it.copy(pendingDeepLink = null) }
        }
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is SetDeepLink -> mutableViewState.update { it.copy(pendingDeepLink = event.route) }
            ConsumeDeepLink -> mutableViewState.update { it.copy(pendingDeepLink = null) }
        }
    }

    data class ViewState(
        val databaseReady: Boolean? = null,
        val theme: Theme = Theme.default(),
        val bottomBarState: BottomBarState = Shown,
        val pendingDeepLink: Route? = null
    )

    sealed interface ViewEvent {
        data class SetDeepLink(val route: Route) : ViewEvent
        data object ConsumeDeepLink : ViewEvent
    }
}
