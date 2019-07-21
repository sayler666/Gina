package com.sayler.app2.data

import android.content.Context
import android.content.SharedPreferences
import com.sayler.data.settings.ISettingsRepository
import com.sayler.data.settings.SettingsRepository
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [SettingsModuleBinds::class])
class SettingsModule {

    @Singleton
    @Provides
    fun provideMoshi() = Moshi.Builder().build()


    @Singleton
    @Provides
    fun provideSharedMemory(context: Context) = context.getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideSettingsRepository(sharedPreferences: SharedPreferences, moshi: Moshi): SettingsRepository {
        return SettingsRepository(sharedPreferences, moshi)
    }

    companion object {
        private const val SETTINGS_PREFERENCES = "SETTINGS_PREFERENCES"
    }
}

@Module
abstract class SettingsModuleBinds {
    @Binds
    abstract fun bindSettingsRepository(settingsRepository: SettingsRepository): ISettingsRepository

}
