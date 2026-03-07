package com.sayler666.gina.attachments.viewmodel

import com.sayler666.core.file.isImageMimeType
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.gina.attachments.ui.AttachmentState

fun Attachment.toState(): AttachmentState {
    requireNotNull(content)
    requireNotNull(mimeType)

    return when {
        mimeType.isImageMimeType() -> AttachmentState.AttachmentImageState(
            id = this.id,
            content = this.content,
            mimeType = this.mimeType,
        )

        else -> AttachmentState.AttachmentNonImageState(
            id = this.id,
            content = content,
            mimeType = mimeType,
            name = mimeType.substringAfter("/")
        )
    }
}
