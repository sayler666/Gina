package com.sayler666.gina.dayDetails

import androidx.compose.runtime.Composable
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.dayDetails.ui.DayDetailsScreen as FeatureDayDetailsScreen

@Composable
fun DayDetailsScreen(route: Route.DayDetails) {
    FeatureDayDetailsScreen(dayId = route.dayId)
}
