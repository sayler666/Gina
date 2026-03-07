package com.sayler666.gina.gallery.di

import com.sayler666.gina.gallery.usecase.ImageAttachmentsRepository
import com.sayler666.gina.gallery.usecase.ImageAttachmentsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GalleryModule {
    @Binds
    abstract fun provideImageAttachmentsRepository(impl: ImageAttachmentsRepositoryImpl): ImageAttachmentsRepository
}
