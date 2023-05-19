package com.sayler666.core.image

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageOptimizationModule {
    @Binds
    @Singleton
    abstract fun provideImageOptimizationSettings(
        settings: ImageOptimizationSettingsImpl
    ): ImageOptimizationSettings
}
