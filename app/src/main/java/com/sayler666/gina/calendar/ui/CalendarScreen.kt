package com.sayler666.gina.calendar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.addDay.ui.AddDayScreenNavArgs
import com.sayler666.gina.calendar.viewmodel.CalendarDayEntity
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.destinations.AddDayScreenDestination
import com.sayler666.gina.destinations.DayDetailsScreenDestination

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
    val days: List<CalendarDayEntity> by viewModel.days.collectAsStateWithLifecycle()

    Scaffold(
        content = { padding ->
            Column(Modifier.fillMaxSize()) {
                if (days.isNotEmpty()) {
                    CalendarVertical(
                        padding,
                        days,
                        onDayClick = { day ->
                            destinationsNavigator.navigate(
                                DayDetailsScreenDestination(DayDetailsScreenNavArgs(day.id))
                            )
                        },
                        onEmptyDayClick = { date ->
                            destinationsNavigator.navigate(
                                AddDayScreenDestination(AddDayScreenNavArgs(date))
                            )
                        },
                        onScrollTop = { viewModel.onViewEvent(OnHideBottomBar) },
                        onScrollBottom = { viewModel.onViewEvent(OnShowBottomBar) },
                    )
                }
            }
        })
}
