package com.sayler666.data.database.db.journal

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sayler666.data.database.db.journal.dao.DaysDao
import com.sayler666.data.database.db.journal.dao.RawDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JournalDatabaseModule {

    @Provides
    @Singleton
    fun provideGinaDatabase(app: Application): GinaDatabase =
        Room.databaseBuilder(
            app, GinaDatabase::class.java,
            app.getDatabasePath(DatabaseFileManager.DB_NAME).absolutePath
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .addMigrations(GinaDatabase.MIGRATION_1_2)
            .build()

    @Provides
    @Singleton
    fun provideDaysDao(db: GinaDatabase): DaysDao = db.daysDao()

    @Provides
    @Singleton
    fun provideRawDao(db: GinaDatabase): RawDao = db.rawDao()

    @Provides
    @Singleton
    fun provideDatabaseFileManager(
        app: Application,
        databaseSettingsStorage: DatabaseSettingsStorage,
        db: GinaDatabase,
        scope: CoroutineScope,
    ): DatabaseFileManager = DatabaseFileManager(app, databaseSettingsStorage, db, scope)

    @Provides
    @Singleton
    fun provideDatabaseSettings(settings: DatabaseSettingsStorageImpl): DatabaseSettingsStorage =
        settings
}
