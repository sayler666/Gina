package com.sayler666.gina.addDay

import androidx.compose.runtime.Composable
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.addDay.ui.AddDayScreen as FeatureAddDayScreen

@Composable
fun AddDayScreen(route: Route.AddDay) {
    FeatureAddDayScreen(date = route.date)
}
