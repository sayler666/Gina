package com.sayler.gina.domain.presenter.day

import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.presenter.BasePresenter
import com.sayler.gina.domain.presenter.BaseView
import org.joda.time.DateTime

/**
 * Created by sayler on 26.01.2018.
 */
class DayContract {

    interface View : BaseView {
        fun show(day: IDay)

        fun noPreviousItemAvailable()

        fun noNextItemAvailable()

        fun timeout()

        fun syntaxError()

        fun error()
    }

    interface Presenter : BasePresenter<View> {
        fun loadById(id: Long)

        fun loadNextAfterDate(dateTime: DateTime)

        fun loadPreviousBeforeDate(dateTime: DateTime)
    }
}