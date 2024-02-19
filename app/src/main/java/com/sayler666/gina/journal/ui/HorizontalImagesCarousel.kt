package com.sayler666.gina.journal.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.sayler666.gina.attachments.ui.PreviousYearsAttachmentThumbnail
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity

data class ImageAttachment(
    val image: AttachmentEntity.Image,
    val yearsAgo: Int
)

typealias HorizontalImagesCarouselState = List<ImageAttachment>

@Composable
fun HorizontalImagesCarousel(
    state: HorizontalImagesCarouselState,
    label: (ImageAttachment) -> String,
    onImageClick: (Int) -> Unit
) {
    LazyRow(contentPadding = PaddingValues(start = 14.dp, end = 14.dp), content = {
        items(state) { attachment ->
            PreviousYearsAttachmentThumbnail(
                attachment.image,
                size = 120.dp,
                text = label(attachment),
                onClick = {
                    attachment.image.id?.let { onImageClick(it) }
                })
        }
    })
}
