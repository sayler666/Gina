package com.sayler666.gina.gallery.viewModel

import com.sayler666.gina.attachments.ui.AttachmentState.AttachmentImageState
import com.sayler666.gina.gallery.usecase.Thumbnail
import com.sayler666.gina.gallery.viewModel.GalleryState.DataState
import com.sayler666.gina.gallery.viewModel.GalleryState.EmptyState
import javax.inject.Inject

class GalleryMapper @Inject constructor() {
    fun toGalleryState(
        attachments: List<Thumbnail>
    ): GalleryState = when {
        attachments.isNotEmpty() -> DataState(
            images = mapAttachments(attachments)
        )

        else -> EmptyState
    }

    private fun mapAttachments(attachments: List<Thumbnail>): List<AttachmentImageState> =
        attachments.map {
            AttachmentImageState(
                id = it.id,
                content = it.bytes,
                mimeType = "image/*",
            )
        }
}

sealed class GalleryState {
    data object LoadingState : GalleryState()
    data object EmptyState : GalleryState()
    data class DataState(
        val images: List<AttachmentImageState>,
    ) : GalleryState()

    data object EmptySearchState : GalleryState()
}
