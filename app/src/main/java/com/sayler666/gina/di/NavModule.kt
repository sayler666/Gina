package com.sayler666.gina.di

import com.sayler666.gina.gameoflife.ui.GameOfLifeScreen
import com.sayler666.gina.navigation.CombinedNavEntryFallback
import com.sayler666.gina.navigation.EntryProviderInstaller
import com.sayler666.gina.navigation.NavEntryFallback
import com.sayler666.gina.navigation.routes.GameOfLife
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object NavModule {

    @Provides
    @IntoSet
    fun provideSystemInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        entry<GameOfLife> { GameOfLifeScreen() }
    }

    @Provides
    fun provideCombinedFallback(
        fallbacks: Set<@JvmSuppressWildcards NavEntryFallback>
    ): @JvmSuppressWildcards CombinedNavEntryFallback = { key ->
        fallbacks.firstNotNullOfOrNull { it(key) } ?: error("Unknown route: $key")
    }
}
