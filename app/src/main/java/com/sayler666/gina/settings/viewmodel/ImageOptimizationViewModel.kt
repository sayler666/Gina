package com.sayler666.gina.settings.viewmodel

import com.sayler666.core.image.ImageOptimization
import com.sayler666.core.viewmodel.ViewModelSlice
import com.sayler666.gina.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ImageOptimizationViewModel : ViewModelSlice {

    fun setNewImageQuality(quality: Int)

    fun toggleImageCompression(enabled: Boolean)

    val imageOptimizationSettings: StateFlow<ImageOptimization.OptimizationSettings?>
}

class ImageOptimizationViewModelImpl @Inject constructor(
    private val setting: Settings,
    override var sliceScope: CoroutineScope
) : ImageOptimizationViewModel {

    private val _tempImageOptimizationSettings: MutableStateFlow<ImageOptimization.OptimizationSettings> =
        MutableStateFlow(ImageOptimization.OptimizationSettings())
    override val imageOptimizationSettings: StateFlow<ImageOptimization.OptimizationSettings?>
        get() = setting.getImageCompressorSettingsFlow().map {
            _tempImageOptimizationSettings.value = it
            it
        }.stateIn(
            sliceScope, SharingStarted.WhileSubscribed(500), null
        )

    override fun setNewImageQuality(quality: Int) {
        val settings = _tempImageOptimizationSettings.value

        sliceScope.launch {
            setting.saveImageCompressorSettings(settings.copy(quality = quality))
        }
    }

    override fun toggleImageCompression(enabled: Boolean) {
        val settings = _tempImageOptimizationSettings.value
        sliceScope.launch {
            setting.saveImageCompressorSettings(settings.copy(compressionEnabled = enabled))
        }
    }
}

