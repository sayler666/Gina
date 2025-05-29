package com.sayler666.data.database.db.quotes

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuotesDatabaseModule {

    @Provides
    @Singleton
    fun provideQuotesDatabaseProvider(app: Application): QuotesDatabaseProvider =
        QuotesDatabaseProvider(app)
}
