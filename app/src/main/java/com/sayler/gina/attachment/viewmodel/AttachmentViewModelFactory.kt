package com.sayler.gina.attachment.viewmodel

import com.sayler.gina.attachment.AttachmentViewModel
import com.sayler.gina.domain.IAttachment

/**
 * Created by sayler on 2017-06-09.
 *

 */
class AttachmentViewModelFactory {
    companion object {
        fun type(attachment: IAttachment, editable: Boolean): AttachmentViewModel {
            return with(attachment) {
                when {
                    mimeType.contains("image/") -> ImageViewModel(attachment, editable)
                    else -> NoImageViewModel(attachment, editable)
                }
            }
        }
    }
}