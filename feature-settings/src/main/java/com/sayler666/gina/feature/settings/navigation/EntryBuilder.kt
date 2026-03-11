package com.sayler666.gina.feature.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.sayler666.gina.feature.settings.ui.SettingsScreen
import com.sayler666.gina.navigation.routes.Route
import com.sayler666.gina.navigation.routes.Settings


fun EntryProviderScope<Route>.featureSettingsEntryBuilder() {
    entry<Settings> { SettingsScreen() }
}