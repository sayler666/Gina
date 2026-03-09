package com.sayler666.gina.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.calendar.viewmodel.CalendarDayEntity
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.mood.ui.awesomeColor
import com.sayler666.gina.mood.ui.badColor
import com.sayler666.gina.mood.ui.emptyLevelColor
import com.sayler666.gina.mood.ui.goodColor
import com.sayler666.gina.mood.ui.lowColor
import com.sayler666.gina.mood.ui.neutralColor
import com.sayler666.gina.mood.ui.superbColor
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.hideNavBar.BOTTOM_NAV_HEIGHT
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val navigator = LocalNavigator.current
    val days by viewModel.days.collectAsStateWithLifecycle()

    if (days.isNotEmpty()) {
        CalendarContent(
            days = days,
            onDayClick = { day -> navigator.navigate(Route.DayDetails(day.id)) },
            onEmptyDayClick = { date -> navigator.navigate(Route.AddDay(date)) },
            onScrollTop = { viewModel.onViewEvent(OnHideBottomBar) },
            onScrollBottom = { viewModel.onViewEvent(OnShowBottomBar) }
        )
    }
}

@Composable
private fun CalendarContent(
    days: List<CalendarDayEntity>,
    onDayClick: (CalendarDayEntity) -> Unit,
    onEmptyDayClick: (LocalDate) -> Unit,
    onScrollBottom: () -> Unit,
    onScrollTop: () -> Unit
) {
    val daysOfWeek = remember { daysOfWeek() }
    val today = remember { LocalDate.now() }
    val currentYearMonth = remember { mutableStateOf(YearMonth.now()) }
    val startMonth = remember { currentYearMonth.value.minusMonths(1000) }
    val endMonth = remember { currentYearMonth }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val coroutineScope = rememberCoroutineScope()
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth.value,
        firstVisibleMonth = currentYearMonth.value,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow
    )
    val visibleMonth = rememberFirstMostVisibleMonth(state)

    val hazeState = rememberHazeState()
    var toolbarHeightPx by remember { mutableStateOf(0) }
    val toolbarHeight = with(LocalDensity.current) { toolbarHeightPx.toDp() }
    val bottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + BOTTOM_NAV_HEIGHT

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta > 30) onScrollTop()
                if (delta < -20) onScrollBottom()
                return Offset.Zero
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        VerticalCalendar(
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .hazeSource(hazeState),
            state = state,
            contentPadding = PaddingValues(top = toolbarHeight, bottom = bottomPadding),
            monthHeader = { MonthHeader(it) },
            dayContent = { calendarDay ->
                val dayEntity = days.firstOrNull { it.date == calendarDay.date }
                CalendarDay(
                    day = calendarDay,
                    dayEntity = dayEntity,
                    today = today,
                    selected = null,
                    onDayClick = onDayClick,
                    onEmptyDayClick = onEmptyDayClick
                )
            },
        )
        Column(
            modifier = Modifier
                .hazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        blurRadius = 24.dp,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        tint = HazeTint(
                            MaterialTheme.colorScheme.surface.copy(alpha = 1f),
                        )
                    )
                ) {
                    progressive =
                        HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                }
                .onSizeChanged { toolbarHeightPx = it.height }
        ) {
            CalendarTopBar(
                visibleMonth,
                onSelectDate = { date ->
                    coroutineScope.launch { state.scrollToMonth(date.yearMonth) }
                },
                onTodayClick = {
                    coroutineScope.launch { state.scrollToMonth(today.yearMonth) }
                }
            )
            WeekDaysHeader(daysOfWeek)
        }
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
    val currentDayColor = MaterialTheme.colorScheme.onBackground
    val monthDate = day.position == DayPosition.MonthDate
    val isSelected = day.date == selected
    if (monthDate)
        Box(
            modifier = Modifier
                .aspectRatio(1.22f)
                .padding(4.dp)
                .clip(shape = RoundedCornerShape(size = 32.dp))
                .conditional(day.date == today) {
                    border(
                        width = 0.5.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.29f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
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
                else -> moodLevelColor(dayEntity?.mood)
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
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun rememberFirstMostVisibleMonth(state: CalendarState): CalendarMonth {
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

@Composable
private fun moodLevelColor(level: Mood?): Color = when (level) {
    Mood.BAD -> badColor()
    Mood.LOW -> lowColor()
    Mood.NEUTRAL -> neutralColor()
    Mood.GOOD -> goodColor()
    Mood.SUPERB -> superbColor()
    Mood.AWESOME -> awesomeColor()
    else -> emptyLevelColor()
}
