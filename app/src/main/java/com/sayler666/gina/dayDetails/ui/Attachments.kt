package com.sayler666.gina.dayDetails.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.file.Files
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity
import com.sayler666.gina.ui.FullScreenDialog
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(Modifier.padding(8.dp).fillMaxSize()) {
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            painter = rememberAsyncImagePainter(attachment.byte),
            contentDescription = "",
        )
    }
}


@Destination(style = FullScreenDialog::class)
@Composable
fun FullImage(
    destinationsNavigator: DestinationsNavigator,
    image: ByteArray,
    mimeType: String
) {
    val context = LocalContext.current
    val (bitmapWidth, bitmapHeight) = BitmapFactory.decodeByteArray(image, 0, image.size).let {
        val (w, h) = it.width to it.height
        it.recycle()
        w to h
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        val (zoomableBox, bottomBar) = createRefs()
        ZoomableBox(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(zoomableBox) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(bottomBar.top)
                    height = Dimension.fillToConstraints
                },
            originalImageHeight = bitmapHeight,
            originalImageWidth = bitmapWidth,
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
        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomBar) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                },
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            actions = {
                IconButton(onClick = {
                    destinationsNavigator.popBackStack()
                    Files.openFileIntent(context, bytes = image, mimeType = mimeType)
                }) {
                    Icon(Icons.Filled.Share, null)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$mimeType : ${image.size / 1024}KB",
                    modifier = Modifier.padding(end = 8.dp)
                )
            })
    }
}
