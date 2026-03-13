package com.sayler666.gina.feature.settings.di

import com.sayler666.gina.feature.settings.AppSettings
import com.sayler666.gina.feature.settings.AppSettingsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun provideAppSettings(appSettingsImpl: AppSettingsImpl): AppSettings
}
