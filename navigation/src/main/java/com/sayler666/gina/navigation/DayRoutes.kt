package com.sayler666.gina.navigation

import com.sayler666.domain.model.Way
import java.time.LocalDate

data class DayDetails(val dayId: Int, val way: Way = Way.NONE) : Route
data class DayDetailsEdit(val dayId: Int) : Route
data class AddDay(val date: LocalDate? = null) : Route
data class ImagePreview(
    val initialAttachmentId: Int,
    val source: ImagePreviewSource,
) : Route
data class ImagePreviewTmp(val image: ByteArray, val mimeType: String) : Route
