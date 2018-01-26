package com.sayler.ormliteimplementation.list.presenter

import com.sayler.gina.domain.presenter.RxPresenter
import com.sayler.gina.domain.presenter.list.ShowListContract
import com.sayler.gina.domain.rx.IRxAndroidTransformer
import com.sayler.ormliteimplementation.entity.Day
import com.sayler.ormliteimplementation.exception.CommunicationError
import com.sayler.ormliteimplementation.exception.OrmLiteError
import com.sayler.ormliteimplementation.list.usecase.GetAllUseCase

/**
 * Created by sayler on 2018-01-26.
 *
 * Copyright 2018 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */

class ShowListPresenter(private val getAllUseCase: GetAllUseCase,
                        private val rxAndroidTransformer: IRxAndroidTransformer)
    : RxPresenter<ShowListContract.View>(), ShowListContract.Presenter {

    override fun loadAll() {
        presenterView?.showProgress()
        val disposable = getAllUseCase
                .getAll()
                .compose(rxAndroidTransformer.applySchedulers())
                .subscribe(::onSuccess, ::onError)
        needToUnsubscribe(disposable)
    }

    override fun loadByTextSearch(searchText: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onSuccess(list: List<Day>?) {
        presenterView?.hideProgress()
        list?.let {
            presenterView?.download(list)
        }
    }

    private fun onError(error: Throwable) {
        presenterView?.hideProgress()
        when (error) {
            is CommunicationError.NoDataSource -> presenterView?.noDataSource()
            is OrmLiteError.TimeoutError -> presenterView?.timeout()
            is OrmLiteError.SyntaxError -> presenterView?.syntaxError()
        }
    }

}