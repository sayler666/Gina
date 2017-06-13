package com.sayler.gina.attachment.viewmodel

import com.sayler.gina.attachment.AttachmentViewModel
import com.sayler.gina.domain.IAttachment

/**
 * Created by sayler on 2017-06-09.
 *
 * Copyright 2017 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class AttachmentViewModelFactory {
    companion object {
        fun type(attachment: IAttachment): AttachmentViewModel {
            return with(attachment) {
                when {
                    mimeType.contains("image/") -> ImageViewModel(attachment)
                    else -> NoImageViewModel(attachment)
                }
            }
        }
    }
}