package com.sayler666.gina.attachments.viewmodel

import com.sayler666.core.file.isImageMimeType
import com.sayler666.gina.db.entity.Attachment
import java.util.Locale
import javax.inject.Inject

class AttachmentMapper @Inject constructor() {
    fun mapToAttachmentEntity(attachment: Attachment): AttachmentEntity {
        requireNotNull(attachment.content)
        requireNotNull(attachment.mimeType)
        return when {
            attachment.mimeType.isImageMimeType() -> AttachmentEntity.Image(
                id = attachment.id,
                bytes = attachment.content,
                mimeType = attachment.mimeType,
                dayId = attachment.dayId
            )

            else -> AttachmentEntity.NonImage(
                id = attachment.id,
                bytes = attachment.content,
                mimeType = attachment.mimeType,
                displayName = attachment.mimeType.substringAfter("/")
                    .uppercase(Locale.getDefault()),
                dayId = attachment.dayId
            )
        }
    }
}
