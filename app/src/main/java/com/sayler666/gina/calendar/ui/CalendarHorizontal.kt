package com.sayler666.gina.calendar.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

@Composable
fun CalendarHorizontal(
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
        outDateStyle = OutDateStyle.EndOfGrid
    )
    val visibleMonth = rememberFirstMostVisibleMonth(state)
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
        HorizontalCalendar(
            modifier = Modifier.padding(8.dp),
            state = state,
            monthHeader = { WeekDaysHeader(daysOfWeek) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(
    visibleMonth: CalendarMonth,
    onSelectDate: (LocalDate) -> Unit,
    onTodayClick: () -> Unit
) {
    val showPopup = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    TopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
            showPopup.value = true
        }) {
            Text(text = visibleMonth.yearMonth.displayText())
            Icon(
                Filled.ArrowDropDown,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
            YearMonthSwitcherPopup(
                showPopup = showPopup.value,
                currentYearMonth = visibleMonth.yearMonth,
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
            Text(
                text = LocalDate.now().dayOfMonth.toString(),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    })
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

    Box(modifier = Modifier
        .aspectRatio(1.22f)
        .padding(1.dp)
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
            monthDate && !isSelected -> MaterialTheme.colorScheme.onSurface
            monthDate && isSelected -> MaterialTheme.colorScheme.surfaceVariant
            !monthDate && !isSelected -> MaterialTheme.colorScheme.outline
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
private fun rememberFirstMostVisibleMonth(
    state: CalendarState,
    viewportPercent: Float = 50f,
): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.firstMostVisibleMonth(viewportPercent) }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

private fun CalendarLayoutInfo.firstMostVisibleMonth(viewportPercent: Float = 50f): CalendarMonth? {
    return if (visibleMonthsInfo.isEmpty()) {
        null
    } else {
        val viewportSize = (viewportEndOffset + viewportStartOffset) * viewportPercent / 100f
        visibleMonthsInfo.firstOrNull { itemInfo ->
            if (itemInfo.offset < 0) {
                itemInfo.offset + itemInfo.size >= viewportSize
            } else {
                itemInfo.size - itemInfo.offset >= viewportSize
            }
        }?.month
    }
}
