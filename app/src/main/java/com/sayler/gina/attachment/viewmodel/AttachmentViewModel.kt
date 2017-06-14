package com.sayler.gina.attachment

import com.sayler.gina.attachment.viewmodel.AttachmentTypesFactory
import com.sayler.gina.domain.IAttachment

/**
 * Created by sayler on 2017-06-09.
 *
 *
 * Copyright 2017 MiQUiDO <http:></http:>//www.miquido.com/>. All rights reserved.
 */
abstract class AttachmentViewModel(val attachment: IAttachment){
    abstract fun type(attachmentTypesFactory: AttachmentTypesFactory): Int
}
