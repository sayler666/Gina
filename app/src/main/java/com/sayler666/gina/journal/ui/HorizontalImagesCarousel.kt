package com.sayler666.gina.journal.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.attachments.ui.PreviousYearsAttachmentThumbnail

data class ImageAttachmentState(
    val state: AttachmentState.AttachmentImageState,
    val yearsAgo: Int
)

typealias HorizontalImagesCarouselState = List<ImageAttachmentState>

@Composable
fun HorizontalImagesCarousel(
    state: HorizontalImagesCarouselState,
    label: (ImageAttachmentState) -> String,
    onImageClick: (Int) -> Unit
) {
    LazyRow(contentPadding = PaddingValues(start = 14.dp, end = 14.dp), content = {
        items(state) { attachment ->
            PreviousYearsAttachmentThumbnail(
                attachment.state,
                size = 120.dp,
                text = label(attachment),
                onClick = {
                    attachment.state.id?.let { onImageClick(it) }
                })
        }
    })
}
