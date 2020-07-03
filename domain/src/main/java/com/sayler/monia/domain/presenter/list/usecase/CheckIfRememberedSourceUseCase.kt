package com.sayler.monia.domain.presenter.list.usecase

import com.sayler.monia.domain.store.settings.SettingsStoreManager
import io.reactivex.Observable

/**
 * Created by sayler on 2018-02-04.
 *
 * Copyright 2018 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class CheckIfRememberedSourceUseCase(val settingsStoreManager: SettingsStoreManager) {
    private val isSourceFileRemembered: Boolean
        get() {
            val settingsStore = settingsStoreManager.get()
            return settingsStore?.dataSourceFilePath != null
        }

    fun check(): Observable<Boolean> {
        if (isSourceFileRemembered) {
            if(settingsStoreManager.get()?.dataSourceFilePath!=null){
                return Observable.just(true)
            }
        }
        return Observable.just(false)
    }
}