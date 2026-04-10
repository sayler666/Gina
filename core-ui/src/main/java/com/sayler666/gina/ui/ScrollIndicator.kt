package com.sayler666.gina.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * A generic fastscroll indicator overlay.
 *
 * @param firstVisibleItemIndex Index of the first fully visible item.
 * @param totalItemsCount Total number of items in the list/grid.
 * @param visibleItemsCount Number of items visible simultaneously (used to clamp fraction to 1.0
 *   when scrolled to the end).
 * @param isScrollInProgress Whether the list is currently being scrolled programmatically or by fling.
 * @param scrollToItem Called when the user drags the handle to a new position.
 * @param labelForIndex Returns the label string to display for a given item index (e.g. "Jan 2024").
 */
@Composable
fun ScrollIndicator(
    firstVisibleItemIndex: Int,
    totalItemsCount: Int,
    visibleItemsCount: Int,
    isScrollInProgress: Boolean,
    scrollToItem: suspend (Int) -> Unit,
    labelForIndex: (Int) -> String,
    modifier: Modifier = Modifier,
) {
    if (totalItemsCount <= 1) return

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    var isDragging by remember { mutableStateOf(false) }
    var isScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(isScrollInProgress) {
        if (isScrollInProgress) {
            isScrolling = true
        } else {
            delay(800)
            isScrolling = false
        }
    }

    val showIndicator = isDragging
    val scrollFraction = firstVisibleItemIndex.toFloat() /
            (totalItemsCount - visibleItemsCount).coerceAtLeast(1)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val trackHeightPx = with(density) { maxHeight.toPx() }
        val thumbHeightPx = with(density) { 48.dp.toPx() }
        val availableTrackPx = (trackHeightPx - thumbHeightPx).coerceAtLeast(0f)

        var dragOffsetPx by remember { mutableFloatStateOf(0f) }

        val thumbTopPx = if (isDragging) dragOffsetPx else scrollFraction * availableTrackPx

        val displayFraction = thumbTopPx / availableTrackPx.coerceAtLeast(1f)
        val displayIndex = (displayFraction * (totalItemsCount - 1))
            .toInt().coerceIn(0, totalItemsCount - 1)
        val labelText = labelForIndex(displayIndex)

        // Track line
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(end = 4.dp)
                .width(3.dp)
                .fillMaxHeight()
                .background(
                    MaterialTheme.colorScheme.onSurface.copy(
                        alpha = when {
                            isDragging -> 0.06f
                            else -> 0.0f
                        }
                    ),
                    RoundedCornerShape(1.5.dp)
                )
        )

        // Draggable touch target (wider than the visual handle for easy grabbing)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(0, thumbTopPx.toInt()) }
                .width(34.dp)
                .height(48.dp)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        val newOffset = (dragOffsetPx + delta).coerceIn(0f, availableTrackPx)
                        dragOffsetPx = newOffset
                        val targetIndex =
                            (newOffset / availableTrackPx.coerceAtLeast(1f) * (totalItemsCount - 1))
                                .toInt().coerceIn(0, totalItemsCount - 1)
                        coroutineScope.launch { scrollToItem(targetIndex) }
                    },
                    onDragStarted = {
                        dragOffsetPx = scrollFraction * availableTrackPx
                        isDragging = true
                    },
                    onDragStopped = { isDragging = false },
                )
        ) {
            // Visual handle bar
            Box(
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp)
                    .width(4.dp)
                    .height(36.dp)
                    .background(
                        if (isDragging) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = if (isScrolling) 0.4f else 0.25f
                        ),
                        RoundedCornerShape(2.dp)
                    )
            )
        }

        // Month/year label — shown when scrolling or dragging, to the left of the handle
        val animatedPadding by animateDpAsState(if (isDragging) 64.dp else 16.dp)

        var showLabel by remember { mutableStateOf(false) }
        val labelVisible = showIndicator && labelText.isNotBlank()
        LaunchedEffect(labelVisible) {
            if (labelVisible) {
                showLabel = true
            } else {
                delay(0.8.seconds)
                showLabel = false
            }
        }

        AnimatedVisibility(
            visible = showLabel && labelText.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(0, thumbTopPx.toInt()) }
                .padding(end = animatedPadding)
                .height(48.dp),
        ) {
            Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxHeight()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = labelText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}
