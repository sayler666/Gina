package com.sayler666.gina.dayDetails.viewmodel

import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.NonImage
import com.sayler666.gina.daysList.viewmodel.Mood
import com.sayler666.gina.daysList.viewmodel.Mood.NEUTRAL
import com.sayler666.gina.db.Attachment
import com.sayler666.gina.db.DayWithAttachment
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale.getDefault
import javax.inject.Inject


class DayDetailsMapper @Inject constructor() {
    fun mapToVm(day: DayWithAttachment): DayWithAttachmentsEntity {
        requireNotNull(day.day.id)
        requireNotNull(day.day.date)
        requireNotNull(day.day.content)
        return DayWithAttachmentsEntity(
                id = day.day.id,
                title = getTitle(day.day.date),
                content = day.day.content,
                attachments = mapAttachments(day.attachments)
        )
    }

    private fun getTitle(timestamp: Long) = Instant.ofEpochSecond(timestamp / 1000)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(
                    DateTimeFormatter.ofPattern("dd. MMMM, yyyy")
            )

    private fun mapAttachments(attachments: List<Attachment>): List<AttachmentEntity> =
            attachments.map {
                requireNotNull(it.content)
                requireNotNull(it.mimeType)
                when {
                    it.mimeType.contains(IMAGE_MIME_TYPE_PREFIX) -> Image(it.content, it.mimeType)
                    else -> NonImage(
                            it.content,
                            it.mimeType,
                            it.mimeType.substringAfter("/")
                                    .uppercase(getDefault())
                    )
                }
            }

    companion object {
        const val IMAGE_MIME_TYPE_PREFIX = "image/"
    }
}

data class DayWithAttachmentsEntity(
        val id: Int,
        val title: String,
        val content: String,
        val mood: Mood = NEUTRAL,
        val attachments: List<AttachmentEntity> = emptyList()
)

sealed class AttachmentEntity(open val byte: ByteArray, open val mimeType: String) {
    data class Image(
            override val byte: ByteArray,
            override val mimeType: String
    ) : AttachmentEntity(byte, mimeType)

    data class NonImage(
            override val byte: ByteArray,
            override val mimeType: String,
            val displayName: String
    ) : AttachmentEntity(byte, mimeType)
}
