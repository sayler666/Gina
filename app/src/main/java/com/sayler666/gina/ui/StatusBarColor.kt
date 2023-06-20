package com.sayler666.gina.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sayler666.gina.settings.Theme

@Composable
fun StatusBarColor(color: Color = MaterialTheme.colorScheme.surface, theme: Theme?) {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, theme) {
        systemUiController.setStatusBarColor(
            color = color,
            darkIcons = color.luminance() > 0.5f
        )
        onDispose {}
    }
}

@Composable
fun NavigationBarColor(
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
    theme: Theme?
) {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, theme) {
        systemUiController.setNavigationBarColor(
            color = color,
            darkIcons = color.luminance() > 0.5f
        )
        onDispose {}
    }
}
