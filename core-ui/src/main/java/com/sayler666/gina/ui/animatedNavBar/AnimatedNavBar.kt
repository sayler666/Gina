package com.sayler666.gina.ui.animatedNavBar

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sayler666.gina.ui.animatedNavBar.animation.balltrajectory.BallAnimInfo
import com.sayler666.gina.ui.animatedNavBar.animation.balltrajectory.BallAnimation
import com.sayler666.gina.ui.animatedNavBar.animation.balltrajectory.Linear
import com.sayler666.gina.ui.animatedNavBar.utils.ballTransform


@Composable
fun AnimatedNavigationBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    barColor: Color,
    ballColor: Color,
    menuItemsSize: Int,
    ballAnimation: BallAnimation = Linear(tween(200)),
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

    Box(
        modifier = modifier
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

val ballSize = 52.dp

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
            .size(width = sizeDp, height = sizeDp * 0.7f)
            .clip(shape = RoundedCornerShape(sizeDp / 2))
            .background(ballColor)
    )
}
