package com.sayler666.gina.ui.animatedNavBar

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sayler666.gina.ui.animatedNavBar.animation.balltrajectory.BallAnimInfo
import com.sayler666.gina.ui.animatedNavBar.animation.balltrajectory.BallAnimation
import com.sayler666.gina.ui.animatedNavBar.animation.balltrajectory.Linear
import com.sayler666.gina.ui.animatedNavBar.utils.ballTransform
import com.sayler666.gina.ui.hideNavBar.BOTTOM_NAV_HEIGHT
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect


@Composable
fun AnimatedNavigationBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    barColor: Color,
    ballColor: Color,
    menuItemsSize: Int,
    ballAnimation: BallAnimation = Linear(tween(200)),
    hazeState: HazeState,
    content: @Composable () -> Unit,
) {

    var itemPositions by remember { mutableStateOf(listOf<Offset>()) }
    val measurePolicy = animatedNavBarMeasurePolicy(menuItemsSize) {
        itemPositions = it.map { xCord ->
            Offset(xCord, 0f)
        }
    }

    val selectedItemOffset by remember(selectedIndex, itemPositions) {
        derivedStateOf {
            if (itemPositions.isNotEmpty()) itemPositions[selectedIndex] else Offset.Unspecified
        }
    }

    val ballAnimInfoState = ballAnimation.animateAsState(targetOffset = selectedItemOffset)

    val shape = RoundedCornerShape(BOTTOM_NAV_HEIGHT / 2)
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = shape,
                clip = false
            )
            .clip(shape)
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    )
                ),
                shape = shape
            )
            .then(modifier)
            .hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    blurRadius = 16.dp,
                    tint = HazeTint(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    )
                )
            )
    ) {
        Layout(
            modifier = Modifier.background(barColor),
            content = {
                content()
                if (ballAnimInfoState.value.offset.isSpecified) {
                    ActiveIndicator(
                        ballAnimInfo = ballAnimInfoState.value,
                        ballColor = ballColor,
                        sizeDp = ballSize
                    )
                }
            },
            measurePolicy = measurePolicy
        )
    }
}

val ballSize = BOTTOM_NAV_HEIGHT-8.dp

@Composable
private fun ActiveIndicator(
    modifier: Modifier = Modifier,
    ballColor: Color,
    ballAnimInfo: BallAnimInfo,
    sizeDp: Dp,
) {
    Box(
        modifier = modifier
            .ballTransform(ballAnimInfo)
            .size(width = sizeDp, height = sizeDp-4.dp)
            .clip(shape = RoundedCornerShape(sizeDp / 2))
            .background(ballColor)
    )
}
