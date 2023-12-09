package com.sayler666.gina.attachments.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileThumbnail(
    attachment: AttachmentEntity.NonImage,
    onClick: (() -> Unit),
    onRemoveClicked: (() -> Unit)? = null
) {
    Card(
        Modifier
            .size(65.dp)
            .padding(end = 8.dp, bottom = 8.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onRemoveClicked?.invoke() }
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Outlined.FileOpen,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                contentDescription = ""
            )
            Text(
                attachment.displayName,
                modifier = Modifier.align(Alignment.BottomEnd),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageThumbnail(
    attachment: AttachmentEntity.Image,
    onClick: (() -> Unit),
    onRemoveClicked: (() -> Unit)? = null,
    size: Dp = 65.dp
) {
    Card(
        Modifier
            .size(size)
            .padding(end = 4.dp, bottom = 4.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onRemoveClicked?.invoke() }
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            painter = rememberAsyncImagePainter(attachment.bytes),
            contentDescription = "",
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviousYearsAttachmentThumbnail(
    attachment: AttachmentEntity.Image,
    text: String,
    onClick: (() -> Unit),
    onRemoveClicked: (() -> Unit)? = null,
    size: Dp = 125.dp
) {
    Card(
        Modifier
            .height(size)
            .width(size * 1.61f)
            .padding(end = 4.dp, bottom = 4.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onRemoveClicked?.invoke() }
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = rememberAsyncImagePainter(attachment.bytes),
                contentDescription = "",
            )
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomStart),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge.copy(
                    shadow = Shadow(color = Color.Black, offset = Offset.Zero, blurRadius = 5f)
                ),
                text = text
            )
        }
    }
}
