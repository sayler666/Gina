package com.sayler666.gina.navigation.routes

import com.sayler666.domain.model.Way
import java.time.LocalDate

data class DayDetails(val dayId: Int, val way: Way = Way.NONE) : Route
data class DayDetailsEdit(val dayId: Int) : Route
data class AddDay(val date: LocalDate? = null) : Route
data class ImagePreview(
    val initialAttachmentId: Int,
    val source: ImagePreviewSource,
) : Route
data class ImagePreviewTmp(
    val image: ByteArray,
    val mimeType: String,
    val attachmentId: Int? = null,
    val hidden: Boolean = false,
) : Route

sealed interface ImagePreviewSource {
    data object Gallery : ImagePreviewSource
    data class Day(val dayId: Int, val attachmentIds: List<Int>) : ImagePreviewSource
    data class Journal(val attachmentIds: List<Int>) : ImagePreviewSource
}
