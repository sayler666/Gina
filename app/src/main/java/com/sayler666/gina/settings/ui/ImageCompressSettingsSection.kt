package com.sayler666.gina.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Rounded
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sayler666.core.image.ImageOptimization.OptimizationSettings
import kotlinx.coroutines.launch

@Composable
fun ImageCompressSettingsSection(
    imageOptimizationSettings: OptimizationSettings?,
    onSetImageQuality: (Int) -> Unit,
    onImageCompressionToggled: (Boolean) -> Unit,
) {
    val showImageCompressSettingsDialog = remember { mutableStateOf(false) }
    imageOptimizationSettings?.let {
        SettingsButton(
            header = "Image optimization",
            body = if (imageOptimizationSettings.compressionEnabled) "Quality: ${it.quality}%" else "Disabled",
            icon = Filled.PhotoSizeSelectLarge,
            onClick = { showImageCompressSettingsDialog.value = true }
        )
        ImageCompressBottomSheet(
            showDialog = showImageCompressSettingsDialog.value,
            imageOptimizationSettings = imageOptimizationSettings,
            onDismiss = { showImageCompressSettingsDialog.value = false },
            onSetImageQuality = onSetImageQuality,
            onImageCompressionToggled = onImageCompressionToggled
        )
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ImageCompressBottomSheet(
    showDialog: Boolean,
    imageOptimizationSettings: OptimizationSettings?,
    onDismiss: () -> Unit,
    onSetImageQuality: (Int) -> Unit,
    onImageCompressionToggled: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    imageOptimizationSettings?.let {
        if (showDialog) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                sheetState = sheetState,
                onDismissRequest = { onDismiss() },
            ) {
                Column {
                    CenterAlignedTopAppBar(
                        title = {
                            Text("Image optimization")
                        }, actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    if (!sheetState.isVisible) onDismiss()
                                }
                            }) {
                                Icon(Rounded.Close, contentDescription = "Close")
                            }
                        }, colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        )
                    )
                    Column(Modifier.padding(horizontal = 8.dp)) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Enable optimization:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Switch(checked = imageOptimizationSettings.compressionEnabled,
                                onCheckedChange = {
                                    onImageCompressionToggled(it)
                                })
                        }
                        var qualitySliderPosition by remember { mutableStateOf(it.quality.toFloat()) }
                        Row(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Quality:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = " ${qualitySliderPosition.toInt()}%",
                                style = MaterialTheme.typography.labelLarge
                                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                        Slider(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            value = qualitySliderPosition,
                            valueRange = 1f..100f,
                            steps = 100,
                            colors = SliderDefaults.colors(
                                activeTickColor = Color.Transparent
                            ),
                            enabled = imageOptimizationSettings.compressionEnabled,
                            onValueChange = { value: Float ->
                                qualitySliderPosition = value
                            },
                            onValueChangeFinished = {
                                onSetImageQuality(qualitySliderPosition.toInt())
                            })
                    }
                }
            }
        }
    }
}
