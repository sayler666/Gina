package com.sayler666.gina.dayDetailsEdit

import androidx.compose.runtime.Composable
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.dayDetailsEdit.ui.DayDetailsEditScreen as FeatureDayDetailsEditScreen

@Composable
fun DayDetailsEditScreen(route: Route.DayDetailsEdit) {
    FeatureDayDetailsEditScreen(dayId = route.dayId)
}
