package com.sayler666.gina.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.addDay.ui.AddDayScreenNavArgs
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.destinations.AddDayScreenDestination
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.calendar.ui.CalendarScreen as FeatureCalendarScreen

@Destination
@Composable
fun CalendarScreen(
    destinationsNavigator: DestinationsNavigator,
    navController: NavController
) {
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavGraphs.root.route)
    }
    val viewModel: CalendarViewModel = hiltViewModel(backStackEntry)
    FeatureCalendarScreen(
        viewModel = viewModel,
        onDayClick = { dayId ->
            destinationsNavigator.navigate(
                DayDetailsScreenDestination(DayDetailsScreenNavArgs(dayId))
            )
        },
        onEmptyDayClick = { date ->
            destinationsNavigator.navigate(
                AddDayScreenDestination(AddDayScreenNavArgs(date))
            )
        }
    )
}
