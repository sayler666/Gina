package com.sayler666.gina.di

import com.sayler666.gina.navigation.CombinedNavEntryFallback
import com.sayler666.gina.navigation.NavEntryFallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NavModule {

    @Provides
    fun provideCombinedFallback(
        fallbacks: Set<@JvmSuppressWildcards NavEntryFallback>
    ): @JvmSuppressWildcards CombinedNavEntryFallback = { key ->
        fallbacks.firstNotNullOfOrNull { it(key) } ?: error("Unknown route: $key")
    }
}
