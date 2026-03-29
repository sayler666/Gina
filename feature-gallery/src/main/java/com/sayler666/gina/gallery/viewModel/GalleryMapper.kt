package com.sayler666.gina.gallery.viewModel

import com.sayler666.gina.gallery.usecase.Thumbnail
import com.sayler666.gina.gallery.viewModel.GalleryState.DataState
import com.sayler666.gina.gallery.viewModel.GalleryState.EmptySearchState
import com.sayler666.gina.gallery.viewModel.GalleryState.EmptyState
import java.time.LocalDate
import javax.inject.Inject

data class GalleryImageState(
    val id: Int?,
    val content: ByteArray,
    val aspectRatio: Float,
    val date: LocalDate,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GalleryImageState

        if (id != other.id) return false
        if (!content.contentEquals(other.content)) return false
        if (aspectRatio != other.aspectRatio) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + content.contentHashCode()
        result = 31 * result + aspectRatio.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}

class GalleryMapper @Inject constructor() {
    fun toGalleryState(
        attachments: List<Thumbnail>,
        filtersActive: Boolean = false,
    ): GalleryState = when {
        attachments.isNotEmpty() -> DataState(
            images = mapAttachments(attachments)
        )

        filtersActive -> EmptySearchState

        else -> EmptyState
    }

    private fun mapAttachments(attachments: List<Thumbnail>): List<GalleryImageState> =
        attachments.map {
            GalleryImageState(
                id = it.id,
                content = it.bytes,
                aspectRatio = it.aspectRatio,
                date = it.date,
            )
        }
}

sealed class GalleryState {
    data object LoadingState : GalleryState()
    data object EmptyState : GalleryState()
    data class DataState(
        val images: List<GalleryImageState>,
    ) : GalleryState()

    data object EmptySearchState : GalleryState()
}
