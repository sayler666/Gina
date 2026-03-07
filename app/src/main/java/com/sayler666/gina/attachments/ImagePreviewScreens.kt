package com.sayler666.gina.attachments

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sayler666.core.compose.ANIMATION_DURATION
import com.sayler666.gina.appDestination
import com.sayler666.gina.attachments.ui.ImagePreviewScreenNavArgs
import com.sayler666.gina.attachments.ui.ImagePreviewTmpScreenNavArgs
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.ImagePreviewScreenDestination
import com.sayler666.gina.destinations.ImagePreviewTmpScreenDestination
import com.sayler666.gina.attachments.ui.ImagePreviewScreen as FeatureImagePreviewScreen
import com.sayler666.gina.attachments.ui.ImagePreviewTmpScreen as FeatureImagePreviewTmpScreen

object ImagePreviewTransitions : DestinationStyle.Animated {

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition()
            : EnterTransition? = when (targetState.appDestination()) {
        ImagePreviewScreenDestination -> scaleIn(animationSpec = tween(ANIMATION_DURATION)) + fadeIn()
        ImagePreviewTmpScreenDestination -> scaleIn(animationSpec = tween(ANIMATION_DURATION)) + fadeIn()

        else -> null
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition()
            : ExitTransition? = when (initialState.appDestination()) {
        ImagePreviewScreenDestination -> scaleOut(animationSpec = tween(ANIMATION_DURATION)) + fadeOut()
        ImagePreviewTmpScreenDestination -> scaleOut(animationSpec = tween(ANIMATION_DURATION)) + fadeOut()

        else -> null
    }
}

@RootNavGraph
@Destination(
    navArgsDelegate = ImagePreviewScreenNavArgs::class,
    style = ImagePreviewTransitions::class
)
@Composable
fun ImagePreviewScreen(
    destinationsNavigator: DestinationsNavigator,
) {
    FeatureImagePreviewScreen(
        onNavigateBack = { destinationsNavigator.popBackStack() },
        onNavigateToDayDetails = { dayId ->
            destinationsNavigator.navigate(
                DayDetailsScreenDestination(DayDetailsScreenNavArgs(dayId = dayId))
            )
        }
    )
}

@RootNavGraph
@Destination(
    navArgsDelegate = ImagePreviewTmpScreenNavArgs::class,
    style = ImagePreviewTransitions::class
)
@Composable
fun ImagePreviewTmpScreen() {
    FeatureImagePreviewTmpScreen()
}
