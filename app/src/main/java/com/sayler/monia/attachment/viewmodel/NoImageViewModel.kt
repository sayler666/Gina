package com.sayler.monia.attachment.viewmodel

import com.sayler.monia.attachment.AttachmentViewModel
import com.sayler.monia.domain.IAttachment

/**
 * Created by sayler on 2017-06-09.
 *

 */
class NoImageViewModel(attachment: IAttachment, editable: Boolean) : AttachmentViewModel(attachment,editable) {
    override fun type(attachmentTypesFactory: AttachmentTypesFactory): Int {
        return attachmentTypesFactory.type(this)
    }
}