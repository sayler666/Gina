package com.sayler666.gina.day.attachments.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil.compose.rememberAsyncImagePainter
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.LocalSharedTransitionScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileThumbnail(
    state: AttachmentState.AttachmentNonImageState,
    onClick: (() -> Unit),
    onRemoveClicked: (() -> Unit)? = null
) {
    val showRemoveMenu = remember { mutableStateOf(false) }
    Box {
        Card(
            Modifier
                .size(65.dp)
                .padding(end = 8.dp, bottom = 8.dp)
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = { if (onRemoveClicked != null) showRemoveMenu.value = true }
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
                    text = state.name,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        RemoveDropdownMenu(
            expanded = showRemoveMenu.value,
            onDismiss = { showRemoveMenu.value = false },
            onRemoveConfirmed = {
                showRemoveMenu.value = false
                onRemoveClicked?.invoke()
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageThumbnail(
    state: AttachmentState.AttachmentImageState,
    onClick: (() -> Unit),
    onRemoveClicked: (() -> Unit)? = null,
    size: Dp = 65.dp
) {
    val sharedScope = LocalSharedTransitionScope.current
    val imageModifier: Modifier = if (sharedScope != null && state.id != null) {
        val sharedState = sharedScope.rememberSharedContentState("attachment_${state.id}")
        val animScope = LocalNavAnimatedContentScope.current
        with(sharedScope) {
            Modifier
                .fillMaxSize()
                .sharedElement(
                    sharedContentState = sharedState,
                    animatedVisibilityScope = animScope
                )
        }
    } else Modifier.fillMaxSize()

    val showRemoveMenu = remember { mutableStateOf(false) }
    Box {
        Card(
            Modifier
                .size(size)
                .padding(end = 4.dp, bottom = 4.dp)
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = { if (onRemoveClicked != null) showRemoveMenu.value = true }
                ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop,
                    painter = rememberAsyncImagePainter(state.content),
                    contentDescription = "",
                )
                if (state.hidden) {
                    Box(Modifier
                        .fillMaxSize()
                        .padding(4.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.VisibilityOff,
                            contentDescription = null,
                            tint = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(20.dp)
                                .offset(x = 0.dp, y = 1.dp)
                                .graphicsLayer {
                                    renderEffect = BlurEffect(2f, 2f, TileMode.Decal)
                                    compositingStrategy = CompositingStrategy.Offscreen
                                }
                                .align(Alignment.TopEnd)
                        )
                        Icon(
                            imageVector = Icons.Rounded.VisibilityOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }
        }
        RemoveDropdownMenu(
            expanded = showRemoveMenu.value,
            onDismiss = { showRemoveMenu.value = false },
            onRemoveConfirmed = {
                showRemoveMenu.value = false
                onRemoveClicked?.invoke()
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviousYearsAttachmentThumbnail(
    state: AttachmentState.AttachmentImageState,
    text: String,
    onClick: (() -> Unit),
    onRemoveClicked: (() -> Unit)? = null,
    size: Dp = 125.dp
) {
    val sharedScope = LocalSharedTransitionScope.current
    val imageModifier: Modifier = if (sharedScope != null && state.id != null) {
        val sharedState = sharedScope.rememberSharedContentState("attachment_${state.id}")
        val animScope = LocalNavAnimatedContentScope.current
        with(sharedScope) {
            Modifier
                .fillMaxSize()
                .sharedElement(
                    sharedContentState = sharedState,
                    animatedVisibilityScope = animScope
                )
        }
    } else Modifier.fillMaxSize()

    val showRemoveMenu = remember { mutableStateOf(false) }
    Box {
        Card(
            Modifier
                .height(size)
                .width(size * 1.61f)
                .padding(end = 4.dp, bottom = 4.dp)
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = { if (onRemoveClicked != null) showRemoveMenu.value = true }
                ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop,
                    painter = rememberAsyncImagePainter(state.content),
                    contentDescription = "",
                )
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomStart),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge.copy(
                        shadow = Shadow(color = Color.Black, offset = Offset.Zero, blurRadius = 5f)
                    ),
                    text = text
                )
            }
        }
        RemoveDropdownMenu(
            expanded = showRemoveMenu.value,
            onDismiss = { showRemoveMenu.value = false },
            onRemoveConfirmed = {
                showRemoveMenu.value = false
                onRemoveClicked?.invoke()
            }
        )
    }
}

@Composable
private fun RemoveDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onRemoveConfirmed: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.day_attachment_remove)) },
            leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            onClick = onRemoveConfirmed,
        )
    }
}
