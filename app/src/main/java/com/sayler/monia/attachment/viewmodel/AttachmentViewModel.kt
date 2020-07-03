package com.sayler.monia.attachment

import com.sayler.monia.attachment.viewmodel.AttachmentTypesFactory
import com.sayler.monia.domain.IAttachment

/**
 * Created by sayler on 2017-06-09.
 *
 */
abstract class AttachmentViewModel(val attachment: IAttachment, val editable: Boolean ){
    abstract fun type(attachmentTypesFactory: AttachmentTypesFactory): Int
}
