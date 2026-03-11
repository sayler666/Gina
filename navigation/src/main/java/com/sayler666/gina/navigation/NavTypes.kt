package com.sayler666.gina.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry

typealias EntryProviderInstaller = EntryProviderScope<Route>.() -> Unit
typealias NavEntryFallback = (Route) -> NavEntry<Route>?
typealias CombinedNavEntryFallback = (Route) -> NavEntry<Route>
