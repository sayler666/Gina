package com.sayler666.gina.dayDetails.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity
import com.sayler666.gina.ui.ZoomableBox


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilePreview(
    attachment: AttachmentEntity.NonImage,
    onClick: (() -> Unit),
    onRemoveClicked: (() -> Unit)? = null
) {
    Card(
        Modifier
            .size(70.dp)
            .padding(end = 8.dp, bottom = 8.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onRemoveClicked?.invoke() }
            ),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Box(Modifier.padding(8.dp)) {
            Icon(
                imageVector = Icons.Outlined.FileOpen,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                contentDescription = ""
            )
            Text(
                attachment.displayName,
                modifier = Modifier.align(BottomEnd),
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
            .size(70.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(end = 8.dp, bottom = 8.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onRemoveClicked?.invoke() }
            ),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Image(
            contentScale = ContentScale.Crop,
            painter = rememberAsyncImagePainter(attachment.byte),
            contentDescription = "",
        )
    }
}


@Destination(style = DestinationStyle.Dialog::class)
@Composable
fun FullImage(
    destinationsNavigator: DestinationsNavigator,
    image: ByteArray
) {
    ZoomableBox(
        outsideImageClick = { destinationsNavigator.popBackStack() }) {
        Image(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            painter = rememberAsyncImagePainter(image),
            contentDescription = null
        )
    }
}
