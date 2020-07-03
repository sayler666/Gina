package com.sayler.monia.attachment.viewmodel

import com.sayler.monia.attachment.AttachmentViewModel
import com.sayler.monia.domain.IAttachment

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