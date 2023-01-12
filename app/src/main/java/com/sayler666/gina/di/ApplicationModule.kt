package com.sayler666.gina.di

import android.app.Application
import com.sayler666.gina.GinaApplication
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
    fun provideCoroutineDispatcherIo(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideExternalScope(application: GinaApplication): CoroutineScope =
        application.applicationScope
}
