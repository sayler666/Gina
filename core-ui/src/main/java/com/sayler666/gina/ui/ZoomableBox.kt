package com.sayler666.gina.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize

@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxScale: Float = 7f,
    click: (() -> Unit)? = null,
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
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    do {
                        val event = awaitPointerEvent()
                        val canceled = event.changes.any { it.isConsumed }
                        if (!canceled) {
                            val zoomChange = event.calculateZoom()
                            val panChange = event.calculatePan()
                            val isMultiTouch = event.changes.count { it.pressed } > 1

                            // Consume only when pinching or already zoomed in.
                            // At scale==1 with single touch, let the pager handle horizontal swipes.
                            if (isMultiTouch || scale > 1f) {
                                if (orientationPortrait) {
                                    scale = maxOf(minScale, minOf(scale * zoomChange, maxScale))
                                    val maxX = (size.width * (scale - 1)) / 2
                                    val minX = -maxX
                                    offsetX = maxOf(minX, minOf(maxX, offsetX + panChange.x))
                                    val maxY = (size.height * (scale - 1)) / 2
                                    val minY = -maxY
                                    offsetY = maxOf(minY, minOf(maxY, offsetY + panChange.y))
                                } else {
                                    scale = maxOf(minScale, minOf(scale * zoomChange, maxScale))
                                    val maxY = (size.height * (scale - 1)) / 2
                                    val minY = -maxY
                                    offsetY = maxOf(minY, minOf(maxY, offsetY + panChange.y))
                                    val maxX = (size.width * (scale - 1)) / 2
                                    val minX = -maxX
                                    offsetX = maxOf(minX, minOf(maxX, offsetX + panChange.x))
                                }
                                event.changes.forEach { if (!it.isConsumed) it.consume() }
                            }
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
            .clickable(interactionSource = interactionSource, indication = null) {
                click?.invoke()
            },
        contentAlignment = Alignment.Center
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
