package com.sayler666.gina.ui

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.sayler666.gina.ui.theme.Theme

@Composable
fun StatusBarColor(color: Color = MaterialTheme.colorScheme.surface, theme: Theme? = LocalTheme.current) {
    val view = LocalView.current
    DisposableEffect(theme) {
        val activity = view.context as Activity
        val controller = WindowCompat.getInsetsController(activity.window, view)
        controller.isAppearanceLightStatusBars = color.luminance() > 0.5f
        onDispose {}
    }
}
