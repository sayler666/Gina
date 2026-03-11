package com.sayler666.gina.feature.setup.di

import com.sayler666.gina.feature.setup.navigation.featureSetupEntryBuilder
import com.sayler666.gina.navigation.EntryProviderInstaller
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object SetupNavModule {

    @Provides
    @IntoSet
    fun provideInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        featureSetupEntryBuilder()
    }
}
