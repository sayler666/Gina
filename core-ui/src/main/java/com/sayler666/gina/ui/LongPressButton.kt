package com.sayler666.gina.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * A button with the same visual appearance as [OutlinedButton], but requires a long press
 * to trigger [onClick]. During the press, a circular fill animates outward from the center.
 * Releasing before the fill completes cancels the action.
 *
 * @param longPressDuration how long (ms) the user must hold before [onClick] fires.
 */
@Composable
fun LongPressButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.outlinedShape,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    longPressDuration: Long = 800L,
    onShortPress: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    val haptics = LocalHapticFeedbackManager.current
    val fillProgress = remember { Animatable(0f) }
    val fillColor = MaterialTheme.colorScheme.secondary

    val colors = ButtonDefaults.buttonColors()
    val containerColor = if (enabled) colors.containerColor else colors.disabledContainerColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor
    val coroutineScope = rememberCoroutineScope()
    var inputPosition = remember { Offset(0f,0f) }

    Surface(
        modifier = modifier
            .padding(top = 4.dp)
            .semantics { role = Role.Button }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    val input = awaitFirstDown(requireUnconsumed = false)
                    inputPosition = input.position
                    val animJob = coroutineScope.launch {
                        fillProgress.snapTo(0f)
                        fillProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = longPressDuration.toInt(),
                                easing = FastOutSlowInEasing
                            )
                        )
                        haptics.tap()
                    }
                    val up = waitForUpOrCancellation()
                    val completed = fillProgress.value >= 1f
                    animJob.cancel()
                    coroutineScope.launch { fillProgress.animateTo(0f, animationSpec = tween(150)) }
                    if (completed && up != null) onClick()
                    else if (!completed && up != null) onShortPress?.invoke()
                }
            },
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = border,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                Row(
                    modifier = Modifier
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        )
                        .drawBehind {
                            if (fillProgress.value > 0f) {
                                val maxRadius = maxOf(
                                    hypot(inputPosition.x, inputPosition.y),
                                    hypot(size.width - inputPosition.x, inputPosition.y),
                                    hypot(inputPosition.x, size.height - inputPosition.y),
                                    hypot(size.width - inputPosition.x, size.height - inputPosition.y)
                                )
                                drawCircle(
                                    color = fillColor,
                                    radius = maxRadius * fillProgress.value,
                                    center = inputPosition
                                )
                            }
                        }
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}
