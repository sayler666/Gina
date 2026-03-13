package com.sayler666.gina.feature.settings.ui

import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.gina.feature.settings.viewmodel.ImageOptimizationViewModel
import com.sayler666.gina.resources.R

@Composable
fun ImageOptimizationSettingsSection(
    viewModel: ImageOptimizationViewModel = hiltViewModel(),
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val showImageCompressSettingsDialog = remember { mutableStateOf(false) }
    viewState.optimizationSettings?.let { settings ->
        SettingsButton(
            header = stringResource(R.string.settings_image_optimization),
            body = if (settings.compressionEnabled) stringResource(R.string.settings_image_quality) + ": ${settings.quality}" + stringResource(
                R.string.settings_image_quality_percent
            ) else stringResource(R.string.settings_image_disabled),
            icon = Filled.PhotoSizeSelectLarge,
            onClick = { showImageCompressSettingsDialog.value = true }
        )
        ImageOptimizationBottomSheet(
            showDialog = showImageCompressSettingsDialog.value,
            onDismiss = { showImageCompressSettingsDialog.value = false },
            viewModel = viewModel,
        )
    }
}


