package com.sayler.gina3.data

import android.content.Context
import android.content.SharedPreferences
import com.sayler.settings.SettingsRepository
import com.sayler.settings.SettingsRepositoryImpl
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideSharedMemory(@ApplicationContext context: Context) =
        context.getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideSettingsRepository(
        sharedPreferences: SharedPreferences,
        moshi: Moshi
    ): SettingsRepository = SettingsRepositoryImpl(sharedPreferences, moshi)

    @Singleton
    @Provides
    fun provideDataManager(dataManager: DataManager) : IDataManager = dataManager

    companion object {
        private const val SETTINGS_PREFERENCES = "SETTINGS_PREFERENCES"
    }

}
