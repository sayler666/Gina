package com.sayler666.gina.calendar

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel
import com.sayler666.gina.calendar.ui.CalendarScreen as FeatureCalendarScreen

@Composable
fun CalendarScreen() {
    val activity = LocalActivity.current as ComponentActivity
    val viewModel: CalendarViewModel = hiltViewModel(activity)
    FeatureCalendarScreen(viewModel = viewModel)
}
