package com.sayler666.gina.calendar.di

import com.sayler666.gina.calendar.navigation.featureCalendarEntryBuilder
import com.sayler666.gina.navigation.EntryProviderInstaller
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object CalendarNavModule {

    @Provides
    @IntoSet
    fun provideInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        featureCalendarEntryBuilder()
    }
}
