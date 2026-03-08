package com.sayler666.gina.settings.ui

import androidx.compose.runtime.Composable
import com.sayler666.gina.ui.LocalTheme
import com.sayler666.gina.feature.settings.ui.SettingsScreen as FeatureSettingsScreen

@Composable
fun SettingsScreen() {
    FeatureSettingsScreen(theme = LocalTheme.current)
}
