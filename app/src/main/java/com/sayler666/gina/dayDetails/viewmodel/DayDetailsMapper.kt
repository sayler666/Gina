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
        //requireNotNull(day.day.id)
        requireNotNull(day.day.date)
        requireNotNull(day.day.content)
        return DayWithAttachmentsEntity(
            id = day.day.id,
            date = getTitle(day.day.date),
            dateTimestamp = day.day.date,
            content = day.day.content,
            attachments = mapAttachments(day.attachments)
        )
    }

    private fun getTitle(timestamp: Long) = Instant.ofEpochSecond(timestamp / 1000)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(
            DateTimeFormatter.ofPattern(DATE_PATTERN)
        )

    private fun mapAttachments(attachments: List<Attachment>): List<AttachmentEntity> =
        attachments.map {
            requireNotNull(it.content)
            requireNotNull(it.mimeType)
            when {
                it.mimeType.contains(IMAGE_MIME_TYPE_PREFIX) -> Image(
                    id = it.id,
                    byte = it.content,
                    mimeType = it.mimeType
                )
                else -> NonImage(
                    id = it.id,
                    byte = it.content,
                    mimeType = it.mimeType,
                    displayName = it.mimeType.substringAfter("/").uppercase(getDefault())
                )
            }
        }

    companion object {
        const val IMAGE_MIME_TYPE_PREFIX = "image/"
        const val DATE_PATTERN = "dd. MMMM, yyyy"
    }
}

data class DayWithAttachmentsEntity(
    val id: Int?,
    val date: String,
    val dateTimestamp: Long,
    val content: String,
    val mood: Mood = NEUTRAL,
    val attachments: List<AttachmentEntity> = emptyList()
)

sealed class AttachmentEntity(
    open val id: Int?,
    open val byte: ByteArray,
    open val mimeType: String
) {
    data class Image(
        override val id: Int? = null,
        override val byte: ByteArray,
        override val mimeType: String
    ) : AttachmentEntity(id, byte, mimeType) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Image

            if (id != other.id) return false
            if (!byte.contentEquals(other.byte)) return false
            if (mimeType != other.mimeType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id ?: 0
            result = 31 * result + byte.contentHashCode()
            result = 31 * result + mimeType.hashCode()
            return result
        }
    }

    data class NonImage(
        override val id: Int? = null,
        override val byte: ByteArray,
        override val mimeType: String,
        val displayName: String
    ) : AttachmentEntity(id, byte, mimeType) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as NonImage

            if (id != other.id) return false
            if (!byte.contentEquals(other.byte)) return false
            if (mimeType != other.mimeType) return false
            if (displayName != other.displayName) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id ?: 0
            result = 31 * result + byte.contentHashCode()
            result = 31 * result + mimeType.hashCode()
            result = 31 * result + displayName.hashCode()
            return result
        }
    }
}
