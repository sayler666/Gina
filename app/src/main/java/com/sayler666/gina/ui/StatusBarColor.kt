package com.sayler666.gina.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun StatusBarColor(color: Color = MaterialTheme.colorScheme.background) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setStatusBarColor(
            color = color,
            darkIcons = useDarkIcons
        )
        onDispose {}
    }
}

@Composable
fun NavigationBarColor(color: Color = MaterialTheme.colorScheme.surfaceVariant) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setNavigationBarColor(
            color = color,
            darkIcons = useDarkIcons
        )
        onDispose {}
    }
}
