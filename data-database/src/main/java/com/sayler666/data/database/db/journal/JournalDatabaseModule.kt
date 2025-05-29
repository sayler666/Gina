package com.sayler666.data.database.db.journal

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JournalDatabaseModule {

    @Provides
    @Singleton
    fun provideJournalDatabaseProvider(
        app: Application,
        databaseSettingsStorage: DatabaseSettingsStorage
    ): GinaDatabaseProvider = GinaDatabaseProvider(app, databaseSettingsStorage)

    @Provides
    @Singleton
    fun provideDatabaseSettings(settings: DatabaseSettingsStorageImpl): DatabaseSettingsStorage =
        settings
}
