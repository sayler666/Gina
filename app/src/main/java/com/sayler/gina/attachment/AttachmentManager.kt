package com.sayler.gina.attachment

import com.sayler.gina.domain.IAttachment
import com.sayler.gina.domain.presenter.Presenter

/**
 * Created by sayler on 03.08.2017.
 */

class AttachmentManager : Presenter<AttachmentManagerContract.View>(), AttachmentManagerContract.Presenter {

    lateinit var attachments: MutableList<IAttachment>

    override fun setup(attachments: MutableList<IAttachment>) {
        this.attachments = attachments

        presenterView.onUpdate(attachments)
    }

    override fun remove(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun add(bytes: ByteArray, mimeType: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}