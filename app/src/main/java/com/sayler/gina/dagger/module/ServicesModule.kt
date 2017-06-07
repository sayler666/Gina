/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.dagger.module;

import android.content.Context;
import com.sayler.gina.store.settings.SPSettingsStoreManager;
import com.sayler.gina.store.settings.SettingsStoreManager;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
class ServicesModule {

    @Singleton
    @Provides
    fun provideSettingsStoreManager(context: Context): SettingsStoreManager {
        return SPSettingsStoreManager(context)
    }
}
