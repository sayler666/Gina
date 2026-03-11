package com.sayler666.gina.gameoflife.di

import com.sayler666.gina.gameoflife.ui.GameOfLifeScreen
import com.sayler666.gina.navigation.EntryProviderInstaller
import com.sayler666.gina.navigation.routes.GameOfLife
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
        entry<GameOfLife> { GameOfLifeScreen(content = it.content) }
    }
}
