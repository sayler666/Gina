package com.sayler666.gina.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.reminder.viewmodel.RemindersViewModel
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
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val themeMapper: ThemeMapper,
    imageOptimizationViewModel: ImageOptimizationViewModel,
    remindersViewModel: RemindersViewModel
) : ViewModel() {

    init {
        with(imageOptimizationViewModel) { initialize() }
        with(remindersViewModel) { initialize() }
    }

    val imageOptimizationVM : ImageOptimizationViewModel = imageOptimizationViewModel
    val remindersVM : RemindersViewModel = remindersViewModel

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
            ginaDatabaseProvider.openDB(path)
        }
    }
}
