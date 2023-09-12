package com.sayler666.core.compose

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable

const val ANIMATION_DURATION = 500

sealed interface Direction
data object Top : Direction
data object Bottom : Direction

fun slideOutVertically(duration: Int = 80, direction: Direction = Bottom) =
    androidx.compose.animation.slideOutVertically(
        targetOffsetY = {
            when (direction) {
                Bottom -> it
                Top -> -it
            }
        },
        animationSpec = tween(durationMillis = duration, easing = FastOutLinearInEasing)
    )

fun slideInVertically(duration: Int = 80, direction: Direction = Bottom) =
    androidx.compose.animation.slideInVertically(
        initialOffsetY = {
            when (direction) {
                Bottom -> it
                Top -> -it
            }
        },
        animationSpec = tween(durationMillis = duration, easing = FastOutLinearInEasing)
    )

fun slideInVerticallyWithFade(direction: Direction = Top) =
    androidx.compose.animation.slideInVertically(
        initialOffsetY = {
            when (direction) {
                Bottom -> it
                Top -> -it
            }
        },
        animationSpec = tween(ANIMATION_DURATION)
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
