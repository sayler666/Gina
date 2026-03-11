package com.sayler666.gina.day.attachments.viewmodel

import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.domain.model.journal.AttachmentWithDay
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.attachments.ui.AttachmentState
import javax.inject.Inject


class ImagePreviewMapper @Inject constructor() {
    fun mapToVm(
        attachmentWithDay: AttachmentWithDay,
    ): ImagePreviewWithDayEntity = with(attachmentWithDay) {
        return ImagePreviewWithDayEntity(
            attachment = attachment.toState(),
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
        attachment = AttachmentState.AttachmentImageState(
            id = null,
            content = image,
            mimeType = mimeType
        ),
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
    val attachment: AttachmentState
    val imageFormat: String
    val imageSize: String
}

data class ImagePreviewTmpEntity(
    override val attachment: AttachmentState,
    override val imageFormat: String,
    override val imageSize: String
) : ImagePreviewEntity

data class ImagePreviewWithDayEntity(
    override val attachment: AttachmentState,
    override val imageFormat: String,
    override val imageSize: String,
    val dayId: Int,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val mood: Mood?
) : ImagePreviewEntity
