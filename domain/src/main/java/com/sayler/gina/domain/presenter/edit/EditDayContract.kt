package com.sayler.gina.domain.presenter.edit

import com.sayler.gina.domain.IAttachment
import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.presenter.BasePresenter
import com.sayler.gina.domain.presenter.BaseView

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
        fun loadById(id: Long)

        fun put(day: IDay, attachments: List<IAttachment>)

        fun delete(day: IDay)
    }
}