package com.sayler666.gina.gallery.viewModel

import com.sayler666.gina.attachments.viewmodel.AttachmentEntity.Image
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

    private fun mapAttachments(attachments: List<Thumbnail>): List<Image> =
        attachments.map {
            Image(
                id = it.id,
                bytes = it.bytes,
                mimeType = "image/*",
                dayId = null
            )
        }
}

sealed class GalleryState {
    object LoadingState : GalleryState()
    object EmptyState : GalleryState()
    data class DataState(
        val images: List<Image>,
    ) : GalleryState()

    object EmptySearchState : GalleryState()
}
