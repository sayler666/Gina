package com.sayler666.gina.gallery.di

import com.sayler666.gina.gallery.navigation.featureGalleryEntryBuilder
import com.sayler666.gina.navigation.EntryProviderInstaller
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object GalleryNavModule {

    @Provides
    @IntoSet
    fun provideInstaller(): @JvmSuppressWildcards EntryProviderInstaller = {
        featureGalleryEntryBuilder()
    }
}
