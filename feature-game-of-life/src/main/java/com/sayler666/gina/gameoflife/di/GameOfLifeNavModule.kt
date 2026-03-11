package com.sayler666.gina.gameoflife.di

import com.sayler666.gina.gameoflife.navigation.featureGameOfLifeEntryBuilder
import com.sayler666.gina.navigation.EntryProviderInstaller
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object GameOfLifeNavModule {

    @Provides
    @IntoSet
    fun provideInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        featureGameOfLifeEntryBuilder()
    }
}
