package com.sayler666.gina.dayDetails

import androidx.compose.runtime.Composable
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.dayDetails.ui.DayDetailsScreen as FeatureDayDetailsScreen

@Composable
fun DayDetailsScreen(route: Route.DayDetails) {
    NavigationBarColor()
    FeatureDayDetailsScreen(dayId = route.dayId)
}
