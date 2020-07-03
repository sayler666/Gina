package com.sayler.monia.attachment

import com.sayler.monia.domain.IAttachment
import com.sayler.monia.domain.ObjectCreator
import com.sayler.monia.domain.presenter.Presenter

/**
 * Created by sayler on 03.08.2017.
 */

class AttachmentManager(val objectCreator: ObjectCreator) : Presenter<AttachmentManagerContract.View>(), AttachmentManagerContract.Presenter {
    lateinit var attachments: MutableCollection<IAttachment>

    override fun setup(attachments: MutableCollection<IAttachment>) {
        this.attachments = attachments

        presenterView.onUpdate(attachments)
    }

    override fun remove(attachment: IAttachment) {
        if (attachments.contains(attachment)) {
            attachments.remove(attachment)
        }
        presenterView.onUpdate(attachments)
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