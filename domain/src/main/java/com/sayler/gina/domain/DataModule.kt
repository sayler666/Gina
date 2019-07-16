package com.sayler.gina.domain

import android.content.Context
import com.sayler.gina.domain.presenter.list.usecase.CalculateStatisticsUseCase
import com.sayler.gina.domain.presenter.list.usecase.CheckIfRememberedSourceUseCase
import com.sayler.gina.domain.presenter.list.usecase.RememberSourceUseCase
import com.sayler.gina.domain.presenter.list.usecase.SetNewSourceUseCase
import com.sayler.gina.domain.rx.IRxAndroidTransformer
import com.sayler.gina.domain.rx.RxAndroidTransformer
import com.sayler.gina.domain.store.settings.SPSettingsStoreManager
import com.sayler.gina.domain.store.settings.SettingsStoreManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class DataModule {

    @Provides
    fun provideIRxAndroidTransformer(): IRxAndroidTransformer {
        return RxAndroidTransformer()
    }

    @Provides
    fun provideCalculateStatisticsUseCase(): CalculateStatisticsUseCase {
        return CalculateStatisticsUseCase()
    }

    @Singleton
    @Provides
    fun provideSettingsStoreManager(context: Context, dataManager: DataManager<*>): SettingsStoreManager {
        val sp = SPSettingsStoreManager(context)
        val settingsStore = sp.get()
        if (settingsStore?.dataSourceFilePath != null) {
            dataManager.setSourceFile(settingsStore?.dataSourceFilePath)
        }
        return sp
    }

    @Provides
    fun provideSetNewSourceUseCase(dataManager: DataManager<*>)
            : SetNewSourceUseCase {
        return SetNewSourceUseCase(dataManager)
    }

    @Provides
    fun provideCheckIfRememberedSourceUseCase(settingsStoreManager: SettingsStoreManager)
            : CheckIfRememberedSourceUseCase {
        return CheckIfRememberedSourceUseCase(settingsStoreManager)
    }

    @Provides
    fun provideRememberSourceUseCase(dataManager: DataManager<*>, settingsStoreManager: SettingsStoreManager)
            : RememberSourceUseCase {
        return RememberSourceUseCase(settingsStoreManager, dataManager)
    }


}