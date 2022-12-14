package com.sayler666.gina.db

import android.app.Application
import com.sayler666.gina.settings.Settings
import com.sayler666.gina.settings.SettingsDataStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSettings(settings: SettingsDataStoreImpl): Settings = settings

    @Provides
    @Singleton
    fun provideDatabaseProvider(app: Application, settings: Settings): DatabaseProvider =
        DatabaseProvider(app, settings)
}
