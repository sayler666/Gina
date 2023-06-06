package com.sayler666.gina.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.image.ImageOptimization.OptimizationSettings
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.settings.Settings
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
    private val setting: Settings, private val databaseProvider: DatabaseProvider
) : ViewModel() {

    private val _tempImageOptimizationSettings: MutableStateFlow<OptimizationSettings> =
        MutableStateFlow(OptimizationSettings())
    val imageOptimizationSettings: StateFlow<OptimizationSettings?>
        get() = setting.getImageCompressorSettingsFlow().map {
            _tempImageOptimizationSettings.value = it
            it
        }.stateIn(
            viewModelScope, WhileSubscribed(500), null
        )

    private val _databasePath: MutableStateFlow<String?> = MutableStateFlow(null)
    val databasePath: StateFlow<String?>
        get() = setting.getDatabasePathFlow().map {
            _databasePath.value = it
            it
        }.stateIn(
            viewModelScope, WhileSubscribed(500), null
        )

    fun setNewImageQuality(quality: Int) {
        val settings = _tempImageOptimizationSettings.value

        viewModelScope.launch {
            setting.saveImageCompressorSettings(settings.copy(quality = quality))
        }
    }

    fun openDatabase(path: String) {
        viewModelScope.launch {
            databaseProvider.openDB(path)
        }
    }

    fun toggleImageCompression(enabled: Boolean) {
        val settings = _tempImageOptimizationSettings.value
        viewModelScope.launch {
            setting.saveImageCompressorSettings(settings.copy(compressionEnabled = enabled))
        }
    }
}
