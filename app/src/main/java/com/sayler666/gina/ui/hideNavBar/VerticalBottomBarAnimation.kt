package com.sayler666.gina.ui.hideNavBar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils.blendARGB
import com.sayler666.gina.ginaApp.BOTTOM_NAV_HEIGHT

@Stable
class VerticalBottomBarAnimation(
    private val animationSpec: FiniteAnimationSpec<Float> = tween(250),
    private val initialOffset: Dp = 0.dp,
    private val maxOffset: Dp = BOTTOM_NAV_HEIGHT,
    private val visibleColor: Color,
    private val hiddenColor: Color,
    private val initialAlpha: Float = 1f
) : BottomBarAnimation {
    @Composable
    override fun animateAsState(visible: Boolean): State<BottomBarAnimInfo> {
        val fraction = remember { Animatable(0f) }

        var offset = remember { maxOffset }
        var color = remember { hiddenColor }
        var alpha = remember { initialAlpha }

        fun measureOffset() {
            offset = maxOffset * fraction.value
        }

        fun measureColor() {
            color = Color(blendARGB(visibleColor.toArgb(), hiddenColor.toArgb(), fraction.value))
        }

        fun measureAlpha() {
            alpha *= 1f - fraction.value
        }

        LaunchedEffect(visible) {
            fraction.animateTo(if (visible) 0f else 1f, animationSpec)
        }

        return produceState(
            initialValue = BottomBarAnimInfo(visibleColor, initialOffset, initialAlpha),
            key1 = offset,
            key2 = color,
            key3 = fraction.value
        ) {
            measureOffset()
            measureColor()
            measureAlpha()

            value = value.copy(
                color = color,
                yOffset = offset,
                alpha = alpha
            )
        }
    }
}
