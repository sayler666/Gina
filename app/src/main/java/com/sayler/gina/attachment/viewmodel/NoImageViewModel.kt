package com.sayler.gina.attachment.viewmodel

import com.sayler.gina.attachment.AttachmentViewModel
import com.sayler.gina.domain.IAttachment

/**
 * Created by sayler on 2017-06-09.
 *

 */
class NoImageViewModel(attachment: IAttachment, editable: Boolean) : AttachmentViewModel(attachment,editable) {
    override fun type(attachmentTypesFactory: AttachmentTypesFactory): Int {
        return attachmentTypesFactory.type(this)
    }
}