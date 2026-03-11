package com.sayler666.gina.calendar.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import com.sayler666.gina.calendar.ui.CalendarScreen
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel
import com.sayler666.gina.navigation.routes.Calendar
import com.sayler666.gina.navigation.routes.Route


fun EntryProviderScope<Route>.featureCalendarEntryBuilder() {
    entry<Calendar> {
        val activity = LocalActivity.current as ComponentActivity
        val viewModel: CalendarViewModel = hiltViewModel(activity)
        CalendarScreen(viewModel = viewModel)
    }
}