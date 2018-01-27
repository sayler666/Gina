package com.sayler.ormliteimplementation.list.presenter

import com.sayler.gina.domain.presenter.RxPresenter
import com.sayler.gina.domain.presenter.list.ShowListContract
import com.sayler.gina.domain.rx.IRxAndroidTransformer
import com.sayler.ormliteimplementation.entity.Day
import com.sayler.ormliteimplementation.exception.CommunicationError
import com.sayler.ormliteimplementation.exception.OrmLiteError
import com.sayler.ormliteimplementation.list.usecase.FindByTextUseCase
import com.sayler.ormliteimplementation.list.usecase.GetAllUseCase

/**
 * Created by sayler on 2018-01-26.
 *
 * Copyright 2018 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */

class ShowListPresenter(private val getAllUseCase: GetAllUseCase,
                        private val findByTextUseCase: FindByTextUseCase,
                        rxAndroidTransformer: IRxAndroidTransformer)
    : RxPresenter<ShowListContract.View>(rxAndroidTransformer), ShowListContract.Presenter {

    override fun loadAll() {
        presenterView?.showProgress()

        getAllUseCase
                .getAll()
                .subscribe(::onSuccess, ::onError)
    }

    override fun loadByTextSearch(searchText: String) {
        presenterView?.showProgress()

        findByTextUseCase
                .findByText(searchText)
                .subscribe(::onSuccess, ::onError)
    }

    private fun onSuccess(list: List<Day>?) {
        presenterView?.hideProgress()
        list?.let {
            presenterView?.show(list)
        }?:presenterView?.error()
    }

    private fun onError(error: Throwable) {
        presenterView?.hideProgress()
        when (error) {
            is CommunicationError.NoDataSource -> presenterView?.noDataSource()
            is OrmLiteError.TimeoutError -> presenterView?.timeout()
            is OrmLiteError.SyntaxError -> presenterView?.syntaxError()
            else -> presenterView?.error()
        }
    }

}