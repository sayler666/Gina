package com.sayler666.gina.imageCompressor

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageCompressorModule {

    @Provides
    @Singleton
    fun provideImageCompressorSettings(settings: ImageCompressorSettingsImpl): ImageCompressorSettings =
        settings

}
