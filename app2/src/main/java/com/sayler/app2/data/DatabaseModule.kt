package com.sayler.app2.data

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(dataManager: DataManager): IDataManager = dataManager
}

