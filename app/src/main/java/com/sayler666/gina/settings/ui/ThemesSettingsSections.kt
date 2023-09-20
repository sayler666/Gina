package com.sayler666.gina.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Rounded
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sayler666.gina.settings.Theme
import com.sayler666.gina.settings.viewmodel.ThemeItem
import kotlinx.coroutines.launch

@Composable
fun ThemesSettingsSections(
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
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            sheetState = sheetState,
            onDismissRequest = { onDismiss() },
        ) {
            Column(
                modifier = Modifier.navigationBarsPadding()
            ) {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets(bottom = 0.dp),
                    title = {
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
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                )

                themes.forEach { theme ->
                    Row(
                        modifier = Modifier.clickable {
                            onSelectTheme(theme.theme)
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = stringResource(id = theme.name),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        RadioButton(
                            modifier = Modifier.padding(end = 8.dp),
                            selected = theme.selected,
                            onClick = {
                                onSelectTheme(theme.theme)
                            }
                        )
                    }
                }
            }
        }
    }
}

