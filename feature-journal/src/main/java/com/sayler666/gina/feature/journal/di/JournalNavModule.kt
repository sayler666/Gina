package com.sayler666.gina.feature.journal.di

import com.sayler666.gina.feature.journal.navigation.featureJournalEntryBuilder
import com.sayler666.gina.navigation.EntryProviderInstaller
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object JournalNavModule {

    @Provides
    @IntoSet
    fun provideInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        featureJournalEntryBuilder()
    }
}
