package com.sayler.gina.attachment

import com.sayler.gina.attachment.viewmodel.AttachmentTypesFactory
import com.sayler.gina.domain.IAttachment

/**
 * Created by sayler on 2017-06-09.
 *
 */
abstract class AttachmentViewModel(val attachment: IAttachment, val editable: Boolean ){
    abstract fun type(attachmentTypesFactory: AttachmentTypesFactory): Int
}
