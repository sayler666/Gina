package com.sayler666.gina.db

import android.app.Application
import com.sayler666.gina.quotes.db.QuotesDatabaseProvider
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
    fun provideDatabaseSettings(settings: DatabaseSettingsImpl): DatabaseSettings = settings

    @Provides
    @Singleton
    fun provideDatabaseProvider(
        app: Application,
        databaseSettings: DatabaseSettings
    ): DatabaseProvider =
        DatabaseProvider(app, databaseSettings)


    @Provides
    @Singleton
    fun provideQuotesDatabaseProvider(app: Application): QuotesDatabaseProvider =
        QuotesDatabaseProvider(app)
}
