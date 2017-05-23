package com.sayler.gina.store.settings

import android.content.Context
import com.sayler.gina.store.SharedPreferencesHelper
import com.sayler.gina.util.Constants

import javax.inject.Singleton

@Singleton
class SPSettingsStoreManager(context: Context) : SettingsStoreManager {
    var sharedPreferencesHelper: SharedPreferencesHelper<SettingsStore> = SharedPreferencesHelper(context, Constants.PREF_SETTINGS_STORE, SettingsStore::class.java)

    override fun get(): SettingsStore? {
        return sharedPreferencesHelper.retrieve()
    }

    override fun save(`object`: SettingsStore) {
        sharedPreferencesHelper.store(`object`)
    }

    override fun clear() {
        sharedPreferencesHelper.clear()
    }

}