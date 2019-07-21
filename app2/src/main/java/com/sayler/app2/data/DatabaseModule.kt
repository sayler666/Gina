package com.sayler.app2.data

import android.content.Context
import com.sayler.data.settings.SettingsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(context: Context, settingsRepository: SettingsRepository): DataManager {
        return DataManager(settingsRepository, context)
    }

}

