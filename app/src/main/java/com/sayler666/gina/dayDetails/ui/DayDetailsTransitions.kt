package com.sayler666.gina.dayDetails.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sayler666.core.compose.ANIMATION_DURATION
import com.sayler666.core.compose.Bottom
import com.sayler666.core.compose.Top
import com.sayler666.core.compose.slideInVerticallyWithFade
import com.sayler666.gina.appDestination
import com.sayler666.gina.destinations.DayDetailsScreenDestination

object DayDetailsTransitions : DestinationStyle.Animated {

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition()
            : EnterTransition? = when (initialState.appDestination()) {
        DayDetailsScreenDestination -> {
            val way = targetState.arguments?.getSerializable(
                DayDetailsScreenNavArgs::way.name,
                Way::class.java
            ) ?: Way.NONE

            when (way) {
                Way.NEXT -> slideInVerticallyWithFade(Bottom)
                Way.PREVIOUS -> slideInVerticallyWithFade(Top)
                Way.NONE -> fadeIn(animationSpec = tween(ANIMATION_DURATION))
            }
        }

        else -> null
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition()
            : ExitTransition? = when (targetState.appDestination()) {
        DayDetailsScreenDestination -> fadeOut(animationSpec = tween(ANIMATION_DURATION))

        else -> null
    }
}
