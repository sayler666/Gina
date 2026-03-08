package com.sayler666.gina.calendar

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel
import com.sayler666.gina.calendar.ui.CalendarScreen as FeatureCalendarScreen

@Composable
fun CalendarScreen() {
    val activity = LocalContext.current as ComponentActivity
    val viewModel: CalendarViewModel = hiltViewModel(activity)
    FeatureCalendarScreen(viewModel = viewModel)
}
