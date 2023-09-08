package com.sayler666.core.compose

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable

sealed interface Direction
data object Top : Direction
data object Bottom : Direction

@Composable
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

@Composable
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
