package com.sayler666.gina.insights.di

import com.sayler666.gina.insights.ui.InsightsScreen
import com.sayler666.gina.navigation.EntryProviderInstaller
import com.sayler666.gina.navigation.routes.Insights
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object InsightsNavModule {

    @Provides
    @IntoSet
    fun provideInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        entry<Insights> { InsightsScreen() }
    }
}
