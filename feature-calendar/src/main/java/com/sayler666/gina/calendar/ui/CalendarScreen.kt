package com.sayler666.gina.calendar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.ui.LocalNavigator

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
) {
    val navigator = LocalNavigator.current
    val days by viewModel.days.collectAsStateWithLifecycle()

    Scaffold(
        content = { padding ->
            Column(Modifier.fillMaxSize()) {
                if (days.isNotEmpty()) {
                    CalendarVertical(
                        padding,
                        days,
                        onDayClick = { day -> navigator.navigate(Route.DayDetails(day.id)) },
                        onEmptyDayClick = { date -> navigator.navigate(Route.AddDay(date)) },
                        onScrollTop = { viewModel.onViewEvent(OnHideBottomBar) },
                        onScrollBottom = { viewModel.onViewEvent(OnShowBottomBar) },
                    )
                }
            }
        })
}
