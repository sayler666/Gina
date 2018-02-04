package com.sayler.gina.domain.presenter.list

import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.presenter.BasePresenter
import com.sayler.gina.domain.presenter.BaseView

/**
 * Created by sayler on 2018-01-26.
 *
 * Copyright 2018 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class ShowListContract {

    interface View : BaseView {
        fun show(dayList: List<IDay>)

        fun statistics(statistics: String)

        fun sourceFileFound()

        fun sourceNoFileFound()

        fun sourceFileSaved()

        fun forgotSourceFile()

        fun timeout()

        fun syntaxError()

        fun error()
    }

    interface Presenter : BasePresenter<View> {
        fun loadAll()

        fun loadByTextSearch(searchText: String)

        fun calculateStatistics()

        fun onCreate()

        fun setNewSource(path: String)

        fun toggleRememberSourceFile()
    }
}