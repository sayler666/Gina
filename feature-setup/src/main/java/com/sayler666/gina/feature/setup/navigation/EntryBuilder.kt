package com.sayler666.gina.feature.setup.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.sayler666.gina.feature.setup.ui.SetupScreen
import com.sayler666.gina.navigation.routes.Route
import com.sayler666.gina.navigation.routes.Startup


fun EntryProviderScope<Route>.featureSetupEntryBuilder() {
    entry<Startup> { SetupScreen() }
}