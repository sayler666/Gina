package com.sayler666.gina.attachments.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilePreview(
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
fun ImagePreview(
    attachment: AttachmentEntity.Image,
    onClick: (() -> Unit),
    onRemoveClicked: (() -> Unit)? = null
) {
    Card(
        Modifier
            .size(65.dp)
            .background(MaterialTheme.colorScheme.background)
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
