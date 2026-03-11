package com.sayler666.gina.day.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.sayler666.core.compose.ANIMATION_DURATION
import com.sayler666.domain.model.Way
import com.sayler666.gina.day.dayDetails.ui.DayDetailsScreen
import com.sayler666.gina.navigation.routes.DayDetails
import com.sayler666.gina.navigation.routes.Route


fun featureDayEntryFallback(key: Route): NavEntry<Route>? = when (key) {
    is DayDetails -> NavEntry(
        key = key,
        metadata = when (key.way) {
            Way.NEXT -> NavDisplay.transitionSpec {
                slideInVertically(tween(ANIMATION_DURATION)) { it } +
                        fadeIn(tween(ANIMATION_DURATION)) togetherWith
                        slideOutVertically(tween(ANIMATION_DURATION)) { -it } +
                        fadeOut(tween(ANIMATION_DURATION))
            }

            Way.PREVIOUS -> NavDisplay.transitionSpec {
                slideInVertically(tween(ANIMATION_DURATION)) { -it } +
                        fadeIn(tween(ANIMATION_DURATION)) togetherWith
                        slideOutVertically(tween(ANIMATION_DURATION)) { it } +
                        fadeOut(tween(ANIMATION_DURATION))
            }

            else -> emptyMap()
        }
    ) {
        DayDetailsScreen(dayId = key.dayId)
    }

    else -> null
}
