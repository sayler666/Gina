package com.sayler666.gina.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import timber.log.Timber

@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxScale: Float = 7f,
    click: (() -> Unit)? = null,
    originalImageHeight: Int,
    originalImageWidth: Int,
    content: @Composable ZoomableBoxScope.() -> Unit
) {
    val configuration = LocalConfiguration.current
    val orientationPortrait by remember {
        mutableStateOf(configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
    }

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .onSizeChanged {
                size = it
                if (orientationPortrait) {
                    val asp = size.width.toFloat() / originalImageWidth
                    offsetY += (size.height - originalImageHeight.toFloat() * asp) / 2
                } else {
                    val asp = size.height.toFloat() / originalImageHeight
                    offsetX += (size.width - originalImageWidth.toFloat() * asp) / 2
                }
                Timber.d("Image: offsetY: $offsetY")
                Timber.d("Image: offsetX: $offsetX")
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    if (orientationPortrait) {
                        scale = maxOf(minScale, minOf(scale * zoom, maxScale))
                        val maxX = (size.width * (scale - 1)) / 2
                        val minX = -maxX
                        offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))
                        offsetY += pan.y
                    } else {
                        scale = maxOf(minScale, minOf(scale * zoom, maxScale))
                        val maxY = (size.width * (scale - 1)) / 2
                        val minY = -maxY
                        offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
                        offsetX += pan.x
                    }
                }
            }
            .clickable(interactionSource = interactionSource, indication = null) {
                click?.invoke()
            }
    ) {
        val scope = ZoomableBoxScopeImpl(scale, offsetX, offsetY)
        scope.content()
    }
}

interface ZoomableBoxScope {
    val scale: Float
    val offsetX: Float
    val offsetY: Float
}

private data class ZoomableBoxScopeImpl(
    override val scale: Float,
    override val offsetX: Float,
    override val offsetY: Float
) : ZoomableBoxScope
