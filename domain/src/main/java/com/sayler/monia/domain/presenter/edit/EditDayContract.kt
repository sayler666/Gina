package com.sayler.monia.domain.presenter.edit

import com.sayler.monia.domain.IAttachment
import com.sayler.monia.domain.IDay
import com.sayler.monia.domain.presenter.BasePresenter
import com.sayler.monia.domain.presenter.BaseView

class EditDayContract {

    interface View : BaseView {

        fun show(day: IDay)

        fun put()

        fun delete()

        fun attachmentTooBig()

        fun timeout()

        fun syntaxError()

        fun error()
    }

    interface Presenter : BasePresenter<View> {
        fun onCreate()

        fun loadById(id: Long)

        fun put(day: IDay, attachments: List<IAttachment>)

        fun delete(day: IDay)
    }
}