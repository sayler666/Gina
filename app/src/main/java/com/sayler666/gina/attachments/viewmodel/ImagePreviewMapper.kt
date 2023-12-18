package com.sayler666.gina.attachments.viewmodel

import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.db.entity.AttachmentWithDay
import com.sayler666.gina.mood.Mood
import javax.inject.Inject


class ImagePreviewMapper @Inject constructor(
    private val attachmentMapper: AttachmentMapper
) {
    fun mapToVm(
        attachmentWithDay: AttachmentWithDay,
    ): ImagePreviewWithDayEntity = with(attachmentWithDay) {
        requireNotNull(day.id)
        requireNotNull(day.date)
        requireNotNull(attachment.id)
        requireNotNull(attachment.mimeType)
        requireNotNull(attachment.content)
        return ImagePreviewWithDayEntity(
            attachment = attachmentMapper.mapToAttachmentEntity(attachment),
            imageFormat = parseMimeType(attachment.mimeType),
            imageSize = getImageSize(attachment.content),
            mood = day.mood,
            dayId = day.id,
            dayOfMonth = getDayOfMonth(day.date),
            dayOfWeek = getDayOfWeek(day.date),
            yearAndMonth = getYearAndMonth(day.date)
        )
    }
}

class ImagePreviewTmpMapper @Inject constructor() {
    fun mapToVm(
        image: ByteArray,
        mimeType: String
    ): ImagePreviewTmpEntity = ImagePreviewTmpEntity(
        attachment = Image(id = null, bytes = image, mimeType = mimeType),
        imageFormat = parseMimeType(mimeType),
        imageSize = getImageSize(image)
    )
}

private fun getImageSize(image: ByteArray) = "${image.size / 1024}KB"

private fun parseMimeType(mimeType: String): String {
    val mimeTypeRegexp = Regex(".+/(\\w+)")
    return mimeType.replace(mimeTypeRegexp) { it.groupValues[1].uppercase() }
}

interface ImagePreviewEntity {
    val attachment: AttachmentEntity
    val imageFormat: String
    val imageSize: String
}

data class ImagePreviewTmpEntity(
    override val attachment: AttachmentEntity,
    override val imageFormat: String,
    override val imageSize: String
) : ImagePreviewEntity

data class ImagePreviewWithDayEntity(
    override val attachment: AttachmentEntity,
    override val imageFormat: String,
    override val imageSize: String,
    val dayId: Int,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val mood: Mood?
) : ImagePreviewEntity
