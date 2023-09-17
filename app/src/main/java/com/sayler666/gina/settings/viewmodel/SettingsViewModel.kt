package com.sayler666.gina.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.settings.Settings
import com.sayler666.gina.settings.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val setting: Settings,
    private val databaseProvider: DatabaseProvider,
    private val themeMapper: ThemeMapper,
    private val imageOptimizationViewModel: ImageOptimizationViewModel
) : ViewModel(), ImageOptimizationViewModel by imageOptimizationViewModel {

    init {
        with(imageOptimizationViewModel) { initialize() }
    }

    private val _databasePath: MutableStateFlow<String?> = MutableStateFlow(null)
    val databasePath: StateFlow<String?> = setting.getDatabasePathFlow().map {
        _databasePath.value = it
        it
    }.stateIn(
        viewModelScope, WhileSubscribed(500), null
    )

    val themes: StateFlow<List<ThemeItem>> = setting.getThemeFlow().map { activeTheme ->
        themeMapper.mapToVM(activeTheme)
    }.stateIn(
        viewModelScope, WhileSubscribed(500), emptyList()
    )

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            setting.saveTheme(theme)
        }
    }

    fun openDatabase(path: String) {
        viewModelScope.launch {
            databaseProvider.openDB(path)
        }
    }
}
