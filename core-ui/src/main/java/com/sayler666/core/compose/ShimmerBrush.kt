package com.sayler666.core.compose

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        val shimmerColors = listOf(
            color.copy(alpha = 0.8f),
            color.copy(alpha = 0.1f),
            color.copy(alpha = 0.8f),
        )

        val transition = rememberInfiniteTransition(label = "shimmer: InfiniteTransition")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800), repeatMode = RepeatMode.Restart
            ),
            label = "shimmer: Translate"
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation.value - targetValue / 2, y = 0f),
            end = Offset(x = translateAnimation.value, y = 0f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}
