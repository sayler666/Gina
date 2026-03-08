package com.sayler666.gina.navigation

import com.sayler666.domain.model.Way
import java.time.LocalDate

sealed interface ImagePreviewSource {
    data object Gallery : ImagePreviewSource
    data class Day(val dayId: Int, val attachmentIds: List<Int>) : ImagePreviewSource
    data class Journal(val attachmentIds: List<Int>) : ImagePreviewSource
}

sealed interface Route {
    // Bottom nav roots
    data object Journal : Route
    data object Calendar : Route
    data object Gallery : Route
    data object Insights : Route
    data object Settings : Route

    // Other screens
    data object SelectDatabase : Route
    data object ManageFriends : Route
    data object GameOfLife : Route

    data class DayDetails(val dayId: Int, val way: Way = Way.NONE) : Route
    data class DayDetailsEdit(val dayId: Int) : Route
    data class AddDay(val date: LocalDate? = null) : Route
    data class ImagePreview(
        val initialAttachmentId: Int,
        val source: ImagePreviewSource,
    ) : Route
    data class ImagePreviewTmp(val image: ByteArray, val mimeType: String) : Route
}
