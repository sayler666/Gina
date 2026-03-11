package com.sayler666.gina.insights.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.sayler666.gina.insights.ui.InsightsScreen
import com.sayler666.gina.navigation.routes.Insights
import com.sayler666.gina.navigation.routes.Route


fun EntryProviderScope<Route>.featureInsightsEntryBuilder() {
    entry<Insights> { InsightsScreen() }
}