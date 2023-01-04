package com.sayler666.gina.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.addDay.ui.AddDayScreenNavArgs
import com.sayler666.gina.calendar.viewmodel.CalendarDayEntity
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel
import com.sayler666.gina.core.compose.conditional
import com.sayler666.gina.core.date.displayText
import com.sayler666.gina.core.date.rememberFirstMostVisibleMonth
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.destinations.AddDayScreenDestination
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun CalendarScreen(
    destinationsNavigator: DestinationsNavigator, navController: NavController
) {
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavGraphs.root.route)
    }
    val viewModel: CalendarViewModel = hiltViewModel(backStackEntry)
    val days: List<CalendarDayEntity> by viewModel.days.collectAsStateWithLifecycle()
    val daysOfWeek = remember { daysOfWeek() }

    val today = remember { LocalDate.now() }
    val currentYearMonth = remember { mutableStateOf(YearMonth.now()) }

    val startMonth = remember { currentYearMonth.value.minusMonths(1000) }
    val endMonth = remember { currentYearMonth }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    val coroutineScope = rememberCoroutineScope()

    if (days.isNotEmpty()) {
        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth.value,
            firstVisibleMonth = currentYearMonth.value,
            firstDayOfWeek = firstDayOfWeek,
            outDateStyle = OutDateStyle.EndOfGrid
        )
        val visibleMonth = rememberFirstMostVisibleMonth(state)
        Column {
            TopBar(visibleMonth,
                currentYearMonth = currentYearMonth.value,
                onSelectDate = { date ->
                    coroutineScope.launch {
                        state.scrollToMonth(date.yearMonth)
                        currentYearMonth.value = date.yearMonth
                    }
                },
                onTodayClick = {
                    coroutineScope.launch {
                        state.scrollToMonth(today.yearMonth)
                        currentYearMonth.value = today.yearMonth
                    }
                })
            HorizontalCalendar(
                state = state,
                monthHeader = { MonthHeader(daysOfWeek) },
                dayContent = { calendarDay ->
                    val dayEntity = days.firstOrNull { it.date == calendarDay.date }
                    Day(calendarDay, dayEntity, today, onDayClick = { day ->
                        destinationsNavigator.navigate(
                            DayDetailsScreenDestination(DayDetailsScreenNavArgs(day.id))
                        )
                    }, onEmptyDayClick = { date ->
                        destinationsNavigator.navigate(
                            AddDayScreenDestination(AddDayScreenNavArgs(date))
                        )
                    })
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    visibleMonth: CalendarMonth,
    currentYearMonth: YearMonth,
    onSelectDate: (LocalDate) -> Unit,
    onTodayClick: () -> Unit
) {
    val showPopup = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    TopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
            showPopup.value = true
        }) {
            Text(text = visibleMonth.displayText())
            Icon(
                Filled.ArrowDropDown,
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = null
            )
            DatePickerPopup(
                showPopup = showPopup.value,
                currentYearMonth = currentYearMonth,
                onDismiss = {
                    scope.launch {
                        delay(100)
                        showPopup.value = false
                    }
                },
                onSelectDate = {
                    onSelectDate(it)
                })
        }
    }, actions = {
        IconButton(onClick = {
            onTodayClick()
        }) {
            Icon(Filled.CalendarToday, null)
        }
    })
}

@Composable
fun Day(
    day: CalendarDay,
    dayEntity: CalendarDayEntity? = null,
    today: LocalDate,
    onDayClick: (CalendarDayEntity) -> Unit,
    onEmptyDayClick: (LocalDate) -> Unit
) {
    val hasEntry = dayEntity != null
    val dotColor = MaterialTheme.colorScheme.primary
    val currentDayColor = MaterialTheme.colorScheme.inversePrimary
    val monthDate = day.position == DayPosition.MonthDate

    Box(modifier = Modifier
        .aspectRatio(1.22f)
        .padding(5.dp)
        .clip(shape = RoundedCornerShape(size = 10.dp))
        .conditional(day.date == today) {
            border(
                width = 1.dp, color = currentDayColor, shape = RoundedCornerShape(10.dp)
            ).background(currentDayColor.copy(alpha = 0.1f))
        }
        .clickable {
            when (dayEntity != null) {
                true -> onDayClick(dayEntity)
                false -> onEmptyDayClick(day.date)
            }
        }, contentAlignment = Alignment.Center
    ) {
        val textColor = when (monthDate) {
            true -> MaterialTheme.colorScheme.onSecondaryContainer
            false -> MaterialTheme.colorScheme.outline
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 4.dp, top = 2.dp)
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor,
                modifier = Modifier
                    .padding(top = 0.dp)
                    .clip(shape = RoundedCornerShape(size = 10.dp))
            )
            if (hasEntry) {
                Icon(
                    painter = rememberVectorPainter(image = Filled.FiberManualRecord),
                    tint = dotColor,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline,
                text = dayOfWeek.displayText(),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
