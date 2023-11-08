package com.sayler666.gina.settings.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sayler666.gina.settings.Theme
import com.sayler666.gina.settings.viewmodel.ColorsPreview
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
                Column {
                    themes.forEach { theme ->
                        Row(
                            modifier = Modifier
                                .clickable { onSelectTheme(theme.theme) }
                                .padding(end = 8.dp, start = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            theme.colorsPreview?.let { ColorsSample(it) }
                            Text(
                                modifier = Modifier.padding(start = if (theme.colorsPreview != null) 8.dp else 0.dp),
                                text = stringResource(id = theme.name),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
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
}

@Composable
fun ColorsSample(color: ColorsPreview) {
    val size = 10.dp
    val thickness = 10.dp
    Box(
        Modifier
            .width(size * 2)
            .height(size * 2)
            .padding(top = size / 2)
    ) {
        val colors = mutableListOf(color.primary, color.secondary, color.tertiary)

        Canvas(
            modifier = Modifier.size(size = size)
        ) {
            var startAngle = -90f
            colors.onEach {
                drawArc(
                    color = it,
                    startAngle = startAngle,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(width = thickness.toPx(), cap = StrokeCap.Butt)
                )
                startAngle += 120f
            }
        }
    }
}

