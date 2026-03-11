package com.sayler666.gina.feature.setup.di

import com.sayler666.gina.feature.setup.ui.SetupScreen
import com.sayler666.gina.navigation.EntryProviderInstaller
import com.sayler666.gina.navigation.routes.Startup
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
        entry<Startup> { SetupScreen() }
    }
}
