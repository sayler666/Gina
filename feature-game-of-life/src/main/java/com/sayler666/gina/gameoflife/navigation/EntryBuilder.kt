package com.sayler666.gina.gameoflife.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.sayler666.gina.gameoflife.ui.GameOfLifeScreen
import com.sayler666.gina.navigation.routes.GameOfLife
import com.sayler666.gina.navigation.routes.Route


fun EntryProviderScope<Route>.featureGameOfLifeEntryBuilder() {
    entry<GameOfLife> { GameOfLifeScreen(content = it.content) }
}