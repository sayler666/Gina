package com.sayler666.gina.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayler666.core.image.ImageOptimization.OptimizationSettings
import com.sayler666.gina.feature.settings.SettingsStorage
import com.sayler666.gina.feature.settings.viewmodel.ImageOptimizationViewModel.ViewEvent.OnImageCompressionToggled
import com.sayler666.gina.feature.settings.viewmodel.ImageOptimizationViewModel.ViewEvent.OnImageQualityChanged
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageOptimizationViewModel @Inject constructor(
    private val setting: SettingsStorage
) : ViewModel() {

    private val mutableViewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = mutableViewState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        setting.getImageCompressorSettingsFlow()
            .onEach { settings -> mutableViewState.update { it.copy(optimizationSettings = settings) } }
            .launchIn(viewModelScope)
    }

    fun onViewEvent(event: ViewEvent) {
        when (event) {
            is OnImageQualityChanged -> setNewImageQuality(event.quality)
            is OnImageCompressionToggled -> toggleImageCompression(event.enabled)
        }
    }

    private fun setNewImageQuality(quality: Int) {
        viewModelScope.launch {
            val settings = mutableViewState.value.optimizationSettings ?: return@launch
            setting.saveImageCompressorSettings(settings.copy(quality = quality))
        }
    }

    private fun toggleImageCompression(enabled: Boolean) {
        viewModelScope.launch {
            val settings = mutableViewState.value.optimizationSettings ?: return@launch
            setting.saveImageCompressorSettings(settings.copy(compressionEnabled = enabled))
        }
    }

    data class ViewState(val optimizationSettings: OptimizationSettings? = null)

    sealed interface ViewEvent {
        data class OnImageQualityChanged(val quality: Int) : ViewEvent
        data class OnImageCompressionToggled(val enabled: Boolean) : ViewEvent
    }
}
