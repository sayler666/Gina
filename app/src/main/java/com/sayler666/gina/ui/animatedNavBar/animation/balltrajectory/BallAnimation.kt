package com.sayler666.gina.ui.animatedNavBar.animation.balltrajectory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset

/**
 * Interface defining the ball animation
 */
interface BallAnimation {

    /**
     *@param [targetOffset] target offset
     */
    @Composable
    fun animateAsState(targetOffset: Offset): State<BallAnimInfo>
}

/**
 * Describes parameters of the ball animation
 */
data class BallAnimInfo(
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val offset: Offset = Offset.Unspecified
)
