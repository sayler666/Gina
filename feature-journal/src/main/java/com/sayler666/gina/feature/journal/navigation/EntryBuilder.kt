package com.sayler666.gina.feature.journal.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.sayler666.gina.feature.journal.ui.JournalScreen
import com.sayler666.gina.navigation.routes.Journal
import com.sayler666.gina.navigation.routes.Route


fun EntryProviderScope<Route>.featureJournalEntryBuilder() {
    entry<Journal> { JournalScreen() }
}