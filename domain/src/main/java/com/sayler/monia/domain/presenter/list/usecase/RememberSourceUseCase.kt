package com.sayler.monia.domain.presenter.list.usecase

import com.sayler.monia.domain.DataManager
import com.sayler.monia.domain.store.settings.SettingsStore
import com.sayler.monia.domain.store.settings.SettingsStoreManager
import io.reactivex.Observable

/**
 * Created by sayler on 2018-02-04.
 *
 * Copyright 2018 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class RememberSourceUseCase(val settingsStoreManager: SettingsStoreManager, val dataManager: DataManager<*>) {

    fun toggle(): Observable<Boolean> {
        if (dataManager.isOpen) {
            //toggle saved file
            var settingsStore = settingsStoreManager.get()
            return if (settingsStore == null) {
                //save current opened file if empty settings store empty
                settingsStore = SettingsStore(dataManager.getSourceFilePath())
                settingsStoreManager.save(settingsStore)
                return Observable.just(true)
            } else {
                //clear current opened file if settings store not empty
                settingsStoreManager.clear()
                return Observable.just(false)
            }
        }
        return Observable.just(false)
    }
}