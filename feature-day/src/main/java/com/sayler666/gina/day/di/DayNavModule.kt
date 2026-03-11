package com.sayler666.gina.day.di

import com.sayler666.gina.day.navigation.featureDayEntryBuilder
import com.sayler666.gina.day.navigation.featureDayEntryFallback
import com.sayler666.gina.navigation.EntryProviderInstaller
import com.sayler666.gina.navigation.NavEntryFallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object DayNavModule {

    @Provides
    @IntoSet
    fun provideInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        featureDayEntryBuilder()
    }

    @Provides
    @IntoSet
    fun provideFallback(): @JvmSuppressWildcards NavEntryFallback = { key ->
        featureDayEntryFallback(key)
    }
}
