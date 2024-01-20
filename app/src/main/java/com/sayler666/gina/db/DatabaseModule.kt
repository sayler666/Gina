package com.sayler666.gina.db

import android.app.Application
import com.sayler666.gina.quotes.db.QuotesDatabaseProvider
import com.sayler666.gina.reminder.db.RemindersDatabaseProvider
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
    fun provideDatabaseSettings(settings: DatabaseSettingsStorageImpl): DatabaseSettingsStorage =
        settings

    @Provides
    @Singleton
    fun provideDatabaseProvider(
        app: Application,
        databaseSettingsStorage: DatabaseSettingsStorage
    ): GinaDatabaseProvider = GinaDatabaseProvider(app, databaseSettingsStorage)

    @Provides
    @Singleton
    fun provideQuotesDatabaseProvider(app: Application): QuotesDatabaseProvider =
        QuotesDatabaseProvider(app)

    @Provides
    @Singleton
    fun provideRemindersDatabaseProvider(app: Application): RemindersDatabaseProvider =
        RemindersDatabaseProvider(app)
}
