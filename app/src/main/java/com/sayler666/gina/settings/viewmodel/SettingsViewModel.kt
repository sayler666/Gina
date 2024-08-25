package com.sayler666.gina.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.withRawDao
import com.sayler666.gina.reminder.viewmodel.RemindersViewModel
import com.sayler666.gina.settings.SettingsStorage
import com.sayler666.gina.settings.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.measureTime

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val setting: SettingsStorage,
    private val ginaDatabaseProvider: GinaDatabaseProvider,
    private val themeMapper: ThemeMapper,
    imageOptimizationViewModel: ImageOptimizationViewModel,
    remindersViewModel: RemindersViewModel
) : ViewModel() {

    init {
        with(imageOptimizationViewModel) { initialize() }
        with(remindersViewModel) { initialize() }
    }

    val imageOptimizationVM: ImageOptimizationViewModel = imageOptimizationViewModel
    val remindersVM: RemindersViewModel = remindersViewModel

    private val _showDbCardLoader: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showDbCardLoader: StateFlow<Boolean> = _showDbCardLoader.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String?>()
    val toastMessage = _toastMessage.asSharedFlow()

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
            ginaDatabaseProvider.openAndRememberDB(path)
        }
    }

    fun vacuumDatabase() {
        viewModelScope.launch {
            Timber.d("Vacuum started")
            _showDbCardLoader.value = true
            val time = measureTime {
                try {
                    ginaDatabaseProvider.withRawDao { vacuum() }
                    _toastMessage.emit("Database vacuumed!")
                } catch (e: Throwable) {
                    _toastMessage.emit("Error while vacuuming!")
                    Timber.e(e)
                } finally {
                    _showDbCardLoader.value = false
                }
            }
            Timber.d("Vacuum ended in: $time")
        }
    }
}
