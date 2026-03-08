package com.sayler666.gina.journal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp),
        content = {
            items(state, key = { it.state.id ?: it.hashCode() }) { attachment ->
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
