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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.compose.scroll.rememberScrollConnection
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.calendar.viewmodel.CalendarDayEntity
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewAction.NavToAddDay
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewAction.NavToDayDetails
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnDayClick
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel.ViewEvent.OnEmptyDayClick
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
fun CalendarScreen(viewModel: CalendarViewModel = hiltViewModel()) {
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    val navigator = LocalNavigator.current

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            is NavToDayDetails -> navigator.navigate(Route.DayDetails(action.dayId))
            is NavToAddDay -> navigator.navigate(Route.AddDay(action.date))
        }
    }

    Content(state = viewState, viewEvent = viewModel::onViewEvent)
}

@Composable
private fun Content(
    state: CalendarViewModel.ViewState,
    viewEvent: (ViewEvent) -> Unit
) {
    if (state.days.isEmpty()) return

    val daysOfWeek = remember { daysOfWeek() }
    val today = remember { LocalDate.now() }
    val currentYearMonth = remember { mutableStateOf(YearMonth.now()) }
    val startMonth = remember { currentYearMonth.value.minusMonths(1000) }
    val endMonth = remember { currentYearMonth }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val coroutineScope = rememberCoroutineScope()
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth.value,
        firstVisibleMonth = currentYearMonth.value,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow
    )
    val visibleMonth = rememberFirstMostVisibleMonth(calendarState)

    val hazeState = rememberHazeState()
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 64.dp
    val bottomPadding = WindowInsets.systemBars.asPaddingValues()
        .calculateBottomPadding() + BOTTOM_NAV_HEIGHT + 64.dp

    val nestedScrollConnection = rememberScrollConnection(
        onScrollUp = { viewEvent(OnHideBottomBar) },
        onScrollDown = { viewEvent(OnShowBottomBar) }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        VerticalCalendar(
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .hazeSource(hazeState),
            state = calendarState,
            contentPadding = PaddingValues(top = topPadding, bottom = bottomPadding),
            monthHeader = { MonthHeader(it) },
            dayContent = { calendarDay ->
                val dayEntity = state.days.firstOrNull { it.date == calendarDay.date }
                CalendarDay(
                    day = calendarDay,
                    dayEntity = dayEntity,
                    today = today,
                    onDayClick = { viewEvent(OnDayClick(it)) },
                    onEmptyDayClick = { viewEvent(OnEmptyDayClick(it)) }
                )
            },
        )
        Column(
            modifier = Modifier
                .hazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        blurRadius = 24.dp,
                        backgroundColor = MaterialTheme.colorScheme.background,
                        tint = HazeTint(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
                        )
                    )
                ) {
                    progressive =
                        HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                }
        ) {
            CalendarTopBar(
                visibleMonth,
                onSelectDate = { date ->
                    coroutineScope.launch { calendarState.scrollToMonth(date.yearMonth) }
                },
                onTodayClick = {
                    coroutineScope.launch { calendarState.scrollToMonth(today.yearMonth) }
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
    onDayClick: (CalendarDayEntity) -> Unit,
    onEmptyDayClick: (LocalDate) -> Unit
) {
    val hasEntry = dayEntity != null
    val monthDate = day.position == DayPosition.MonthDate
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
                .clickable {
                    when (dayEntity != null) {
                        true -> onDayClick(dayEntity)
                        false -> onEmptyDayClick(day.date)
                    }
                }, contentAlignment = Alignment.Center
        ) {

            Day(
                day = day,
                textColor = MaterialTheme.colorScheme.onSurface,
                isSelected = false,
                hasEntry = hasEntry,
                dotColor = moodLevelColor(dayEntity?.mood)
            )
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
