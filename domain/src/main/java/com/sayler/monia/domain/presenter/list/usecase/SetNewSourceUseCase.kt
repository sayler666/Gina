package com.sayler.monia.domain.presenter.list.usecase

import com.sayler.monia.domain.DataManager
import io.reactivex.Completable

/**
 * Created by sayler on 2018-02-04.
 *
 * Copyright 2018 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class SetNewSourceUseCase(val dataManager: DataManager<*>) {

    fun set(path:String): Completable{
        dataManager.setSourceFile(path)
        return Completable.complete()
    }
}