package com.sayler666.gina.dayDetails.viewmodel

import com.sayler666.gina.core.date.toLocalDate
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.NonImage
import com.sayler666.gina.db.Attachment
import com.sayler666.gina.db.DayWithAttachment
import com.sayler666.gina.ui.Mood
import com.sayler666.gina.ui.Mood.Companion.mapToMoodOrNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale.getDefault
import javax.inject.Inject


class DayDetailsMapper @Inject constructor() {
    fun mapToVm(day: DayWithAttachment): DayWithAttachmentsEntity {
        requireNotNull(day.day.date)
        requireNotNull(day.day.content)
        return DayWithAttachmentsEntity(
            id = day.day.id,
            dayOfMonth = getDayOfMonth(day.day.date),
            dayOfWeek = getDayOfWeek(day.day.date),
            yearAndMonth = getYearAndMonth(day.day.date),
            localDate = getLocalDate(day.day.date),
            content = day.day.content,
            attachments = mapAttachments(day.attachments),
            mood = day.day.mood.mapToMoodOrNull()
        )
    }

    private fun getLocalDate(timestamp: Long) = timestamp.toLocalDate()

    private fun getDayOfMonth(timestamp: Long) = timestamp.toLocalDate()
        .format(
            DateTimeFormatter.ofPattern("dd")
        )

    private fun getDayOfWeek(timestamp: Long) = timestamp.toLocalDate()
        .format(
            DateTimeFormatter.ofPattern("EEEE")
        )

    private fun getYearAndMonth(timestamp: Long) = timestamp.toLocalDate()
        .format(
            DateTimeFormatter.ofPattern("yyyy, MMMM")
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
    }
}

data class DayWithAttachmentsEntity(
    val id: Int?,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val localDate: LocalDate,
    val content: String,
    val mood: Mood?,
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
