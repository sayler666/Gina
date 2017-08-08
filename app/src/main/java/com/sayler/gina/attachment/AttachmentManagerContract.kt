package com.sayler.gina.attachment

import com.sayler.gina.domain.IAttachment
import com.sayler.gina.domain.presenter.BasePresenter
import com.sayler.gina.domain.presenter.BaseView

/**
 * Created by sayler on 03.08.2017.
 */

class AttachmentManagerContract {
    interface View : BaseView {
        fun onUpdate(attachments: MutableCollection<IAttachment>)
    }

    interface Presenter : BasePresenter<AttachmentManagerContract.View> {
        fun add(bytes: ByteArray, mimeType: String)
        fun remove(id: Int)
        fun setup(attachments: MutableCollection<IAttachment>)
    }
}
