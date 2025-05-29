package com.sayler666.data.database.db.reminders

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemindersDatabaseModule {

    @Provides
    @Singleton
    fun provideRemindersDatabaseProvider(app: Application): RemindersDatabaseProvider =
        RemindersDatabaseProvider(app)
}
