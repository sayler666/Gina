package com.sayler666.gina.settings.ui

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Rounded
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.file.Files
import com.sayler666.core.image.ImageOptimization.OptimizationSettings
import com.sayler666.gina.destinations.ManageFriendsScreenDestination
import com.sayler666.gina.settings.Theme
import com.sayler666.gina.settings.viewmodel.SettingsViewModel
import com.sayler666.gina.settings.viewmodel.ThemeItem
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination
@Composable
fun SettingsScreen(
    destinationsNavigator: DestinationsNavigator,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val imageOptimizationSettings: OptimizationSettings? by viewModel.imageOptimizationSettings.collectAsStateWithLifecycle()
    val databasePath: String? by viewModel.databasePath.collectAsStateWithLifecycle()
    val themes: List<ThemeItem> by viewModel.themes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Database",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                DatabaseSettingsSection(
                    databasePath,
                    onNewDbFileSelected = { path ->
                        viewModel.openDatabase(path)
                    }
                )
                FriendsSettingsSections(destinationsNavigator)
                Text(
                    text = "Attachments",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ImageCompressSettingsSection(
                    imageOptimizationSettings,
                    onSetImageQuality = viewModel::setNewImageQuality,
                    onImageCompressionToggled = viewModel::toggleImageCompression
                )
                Text(
                    text = "Personalize",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ThemesSettingsSections(themes) { theme ->
                    viewModel.setTheme(theme)
                }
            }
        })
}

@Composable
private fun DatabaseSettingsSection(
    databasePath: String?,
    onNewDbFileSelected: (String) -> Unit
) {
    val databaseResult = rememberLauncherForActivityResult(StartActivityForResult()) {
        it.data?.data?.path?.let { path -> onNewDbFileSelected(path) }
    }
    SettingsButton(
        header = "Database file",
        body = databasePath ?: "",
        icon = Filled.Book,
        onClick = {
            databaseResult.launch(Files.selectFileIntent())
        }
    )
}

@Composable
private fun FriendsSettingsSections(
    destinationsNavigator: DestinationsNavigator
) {
    SettingsButton(
        header = "Friends",
        body = "Manage friends list",
        icon = Filled.People,
        onClick = {
            destinationsNavigator.navigate(ManageFriendsScreenDestination)
        }
    )
}

@Composable
private fun ImageCompressSettingsSection(
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
        ImageCompressSettingsDialog(
            showDialog = showImageCompressSettingsDialog.value,
            imageOptimizationSettings = imageOptimizationSettings,
            onDismiss = { showImageCompressSettingsDialog.value = false },
            onSetImageQuality = onSetImageQuality,
            onImageCompressionToggled = onImageCompressionToggled
        )
    }
}

@Composable
private fun ThemesSettingsSections(
    themes: List<ThemeItem>,
    onThemeSelected: (Theme) -> Unit
) {
    val scope = rememberCoroutineScope()
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val current = themes.firstOrNull { it.selected }?.name
    SettingsButton(
        header = "Theme",
        body = current?.let { stringResource(id = it) } ?: "Theme",
        icon = Filled.ColorLens,
        onClick = {
            openBottomSheet = true
        }
    )
    ThemesBottomSheet(
        themes,
        openBottomSheet,
        onDismiss = { scope.launch { openBottomSheet = false } },
        onSelectTheme = onThemeSelected
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ThemesBottomSheet(
    themes: List<ThemeItem>,
    openBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onSelectTheme: (Theme) -> Unit
) {
    val scope = rememberCoroutineScope()

    if (openBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { onDismiss() },
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                CenterAlignedTopAppBar(title = {
                    Text("Theme")
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
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
                )
            }

            themes.forEach { theme ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable {
                            onSelectTheme(theme.theme)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(id = theme.name),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    RadioButton(selected = theme.selected,
                        onClick = {
                            onSelectTheme(theme.theme)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsButton(
    header: String,
    body: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .clip(shape = CircleShape)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Icon(icon, null, Modifier.padding(10.dp))
            }
            Column(modifier = Modifier.padding(0.dp)) {
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelLarge
                        .copy(color = MaterialTheme.colorScheme.onBackground)
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.labelMedium
                        .copy(color = MaterialTheme.colorScheme.outline)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Filled.ChevronRight, null, Modifier.padding(end = 8.dp))
        }
    }
}

@Composable
fun ImageCompressSettingsDialog(
    showDialog: Boolean,
    imageOptimizationSettings: OptimizationSettings?,
    onDismiss: () -> Unit,
    onSetImageQuality: (Int) -> Unit,
    onImageCompressionToggled: (Boolean) -> Unit,
) {
    if (showDialog) {
        imageOptimizationSettings?.let {
            Dialog(onDismissRequest = { onDismiss() }) {
                Card(Modifier.padding(0.dp)) {
                    Column(Modifier.padding(horizontal = 8.dp)) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Image Optimization:",
                                style = MaterialTheme.typography.labelLarge
                                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
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
                                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                            Text(
                                text = " ${qualitySliderPosition.toInt()}%",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.W900)
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
