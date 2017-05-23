package com.sayler.gina.store.settings

import android.content.Context
import com.sayler.gina.store.SharedPreferenceHelper
import com.sayler.gina.util.Constants

import javax.inject.Singleton

@Singleton
class SPSettingsStoreManager(context: Context) : SettingsStoreManager {
    var sharedPreferenceHelper: SharedPreferenceHelper<SettingsStore> = SharedPreferenceHelper(context, Constants.PREF_SETTINGS_STORE, SettingsStore::class.java)

    override fun get(): SettingsStore? {
        return sharedPreferenceHelper.retrieve()
    }

    override fun save(`object`: SettingsStore) {
        sharedPreferenceHelper.store(`object`)
    }

    override fun clear() {
        sharedPreferenceHelper.clear()
    }

}