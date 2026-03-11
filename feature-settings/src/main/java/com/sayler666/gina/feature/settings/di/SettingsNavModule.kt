package com.sayler666.gina.feature.settings.di

import com.sayler666.gina.feature.settings.ui.SettingsScreen
import com.sayler666.gina.navigation.EntryProviderInstaller
import com.sayler666.gina.navigation.Settings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object SettingsNavModule {

    @Provides
    @IntoSet
    fun provideInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        entry<Settings> { SettingsScreen() }
    }
}
