package com.sayler666.gina.navigation

sealed interface ImagePreviewSource {
    data object Gallery : ImagePreviewSource
    data class Day(val dayId: Int, val attachmentIds: List<Int>) : ImagePreviewSource
    data class Journal(val attachmentIds: List<Int>) : ImagePreviewSource
}
