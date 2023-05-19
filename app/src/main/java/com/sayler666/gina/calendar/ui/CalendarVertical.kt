package com.sayler666.gina.calendar.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import com.sayler666.core.compose.conditional
import com.sayler666.gina.calendar.viewmodel.CalendarDayEntity
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarVertical(
    days: List<CalendarDayEntity>,
    onDayClick: (CalendarDayEntity) -> Unit,
    onEmptyDayClick: (LocalDate) -> Unit,
    selectable: Boolean = false,
    selectedDate: LocalDate? = null,
    firstVisible: LocalDate? = null
) {
    val daysOfWeek = remember { daysOfWeek() }
    val today = remember { LocalDate.now() }
    val currentYearMonth = remember { mutableStateOf(YearMonth.now()) }
    val startMonth = remember { currentYearMonth.value.minusMonths(1000) }
    val endMonth = remember { currentYearMonth }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val selected = remember { mutableStateOf(selectedDate ?: LocalDate.now()) }

    val coroutineScope = rememberCoroutineScope()
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth.value,
        firstVisibleMonth = firstVisible?.yearMonth ?: currentYearMonth.value,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow
    )
    val visibleMonth = rememberFirstMostVisibleYear(state)

    Column {
        CalendarTopBar(visibleMonth,
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
        WeekDaysHeader(daysOfWeek)
        VerticalCalendar(
            state = state,
            contentPadding = PaddingValues(bottom = 100.dp),
            monthHeader = { MonthHeader(it) },
            dayContent = { calendarDay ->
                val dayEntity = days.firstOrNull { it.date == calendarDay.date }
                CalendarDay(
                    calendarDay,
                    dayEntity,
                    today,
                    if (selectable) selected.value else null,
                    onDayClick = { day ->
                        onDayClick(day)
                        if (selectable) selected.value = day.date
                    },
                    onEmptyDayClick = { date ->
                        onEmptyDayClick(date)
                        if (selectable) selected.value = date
                    })
            },
        )
    }
}

@Composable
private fun CalendarDay(
    day: CalendarDay,
    dayEntity: CalendarDayEntity? = null,
    today: LocalDate,
    selected: LocalDate? = null,
    onDayClick: (CalendarDayEntity) -> Unit,
    onEmptyDayClick: (LocalDate) -> Unit
) {
    val hasEntry = dayEntity != null
    val currentDayColor = MaterialTheme.colorScheme.surfaceTint
    val monthDate = day.position == DayPosition.MonthDate
    val isSelected = day.date == selected
    if (monthDate)
        Box(modifier = Modifier
            .aspectRatio(1.22f)
            .padding(4.dp)
            .clip(shape = RoundedCornerShape(size = 8.dp))
            .conditional(day.date == today) {
                border(
                    width = 1.dp,
                    color = currentDayColor,
                    shape = RoundedCornerShape(8.dp)
                ).background(currentDayColor.copy(alpha = 0.1f))
            }
            .conditional(isSelected) {
                border(
                    width = 1.dp,
                    color = currentDayColor,
                    shape = RoundedCornerShape(8.dp)
                ).background(currentDayColor.copy(alpha = 0.9f))
            }
            .clickable {
                when (dayEntity != null) {
                    true -> onDayClick(dayEntity)
                    false -> onEmptyDayClick(day.date)
                }
            }, contentAlignment = Alignment.Center
        ) {
            val textColor = when {
                !isSelected -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.surface
            }

            val dotColor = when {
                isSelected -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.primary
            }

            Day(day, textColor, isSelected, hasEntry, dotColor)
        }
}

@Composable
private fun MonthHeader(calendarMonth: CalendarMonth) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = calendarMonth.yearMonth.displayText(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun rememberFirstMostVisibleYear(
    state: CalendarState
): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.firstMostVisibleMonth() }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

private fun CalendarLayoutInfo.firstMostVisibleMonth(): CalendarMonth? =
    if (visibleMonthsInfo.isEmpty()) {
        null
    } else {
        visibleMonthsInfo.firstOrNull()?.month
    }
