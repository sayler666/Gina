package com.sayler666.gina.di

import android.app.Application
import android.content.Context
import com.sayler666.gina.ginaApp.GinaApplication
import com.sayler666.gina.settings.AppSettings
import com.sayler666.gina.settings.AppSettingsImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    @Singleton
    fun provideGinaApplication(application: Application): GinaApplication =
        application as GinaApplication

    @Provides
    @Singleton
    fun provideContext(application: Application): Context =
        application

    @Provides
    @Singleton
    fun provideCoroutineDispatcherIo(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideExternalScope(application: GinaApplication): CoroutineScope =
        application.applicationScope

    @Provides
    @Singleton
    fun provideAppSettings(appSettingsImpl: AppSettingsImpl): AppSettings = appSettingsImpl
}
