package com.sayler.monia.attachment

import com.sayler.monia.domain.IAttachment
import com.sayler.monia.domain.presenter.BasePresenter
import com.sayler.monia.domain.presenter.BaseView

/**
 * Created by sayler on 03.08.2017.
 */

class AttachmentManagerContract {
    interface View : BaseView {
        fun onUpdate(attachments: MutableCollection<IAttachment>)
    }

    interface Presenter : BasePresenter<AttachmentManagerContract.View> {
        fun add(bytes: ByteArray, mimeType: String)
        fun remove(attachment: IAttachment)
        fun getAll(): MutableCollection<IAttachment>
        fun setup(attachments: MutableCollection<IAttachment>)
    }
}
