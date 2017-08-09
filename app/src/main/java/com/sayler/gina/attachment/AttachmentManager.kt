package com.sayler.gina.attachment

import com.sayler.gina.domain.IAttachment
import com.sayler.gina.domain.ObjectCreator
import com.sayler.gina.domain.presenter.Presenter

/**
 * Created by sayler on 03.08.2017.
 */

class AttachmentManager(val objectCreator: ObjectCreator) : Presenter<AttachmentManagerContract.View>(), AttachmentManagerContract.Presenter {
    lateinit var attachments: MutableCollection<IAttachment>

    override fun setup(attachments: MutableCollection<IAttachment>) {
        this.attachments = attachments

        presenterView.onUpdate(attachments)
    }

    override fun remove(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun add(bytes: ByteArray, mimeType: String) {
        objectCreator.createAttachment().let {
            it.file = bytes
            it.mimeType = mimeType
            attachments.add(it)
        }

        //update view
        presenterView.onUpdate(attachments)
    }

    override fun getAll(): MutableCollection<IAttachment> {
        return attachments
    }

}