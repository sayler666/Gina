package com.sayler666.gina.insights.ui

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapWeek
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import com.sayler666.gina.core.date.displayText
import com.sayler666.gina.insights.viewmodel.ContributionLevel
import com.sayler666.gina.insights.viewmodel.InsightState
import com.sayler666.gina.insights.viewmodel.InsightState.DataState
import com.sayler666.gina.insights.viewmodel.InsightsViewModel
import com.sayler666.gina.insights.viewmodel.Level
import com.sayler666.gina.insights.viewmodel.MoodChartData
import com.sayler666.gina.insights.viewmodel.MoodLevel
import com.sayler666.gina.insights.viewmodel.Zero
import com.sayler666.gina.journal.ui.EmptyResult
import com.sayler666.gina.ui.FiltersBar
import com.sayler666.gina.ui.Mood
import com.sayler666.gina.ui.mapToMoodIcon
import java.time.LocalDate
import java.time.YearMonth

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class
)
@com.ramcosta.composedestinations.annotation.Destination
@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val state: InsightState by viewModel.state.collectAsStateWithLifecycle()
    val searchText = rememberSaveable { mutableStateOf("") }
    val moodsFilters: List<Mood> by viewModel.moodFilters.collectAsStateWithLifecycle()
    val filtersActive: Boolean by viewModel.filtersActive.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            FiltersBar(
                title = "Insights",
                searchText = searchText.value,
                onSearchTextChanged = {
                    searchText.value = it
                    viewModel.searchQuery(searchText.value)
                },
                onClearClick = {
                    viewModel.searchQuery("")
                    searchText.value = ""
                },
                moodFilters = moodsFilters,
                onMoodFiltersUpdate = { moods ->
                    viewModel.updateMoodFilters(moods)
                },
                onResetFiltersClicked = {
                    viewModel.resetFilters()
                },
                filtersActive
            )
        },
        content = { padding ->
            Insights(padding, state)
        })
}

@Composable
private fun Insights(
    padding: PaddingValues,
    state: InsightState
) {
    when (state) {
        is DataState -> Render(state, padding)
        InsightState.EmptySearchState -> EmptyResult(
            "Empty search result!",
            "Try narrowing search criteria."
        )

        InsightState.EmptyState -> EmptyResult(
            "No data found!",
            "Add some entries."
        )
    }
}

@Composable
fun Render(state: DataState, padding: PaddingValues) {
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(scrollState)
    ) {
        Summary(state)
        HeatMapCalendar(
            state.contributionHeatMapData,
            "Contributions",
            "Less",
            "More",
            arrayOf(*ContributionLevel.values())
        )
        HeatMapCalendar(
            state.moodHeatMapData,
            "Moods",
            "Worse",
            "Better",
            arrayOf(*MoodLevel.values())
        )
        DoughnutChart(state.moodChartData)
        Spacer(modifier = Modifier.height(34.dp))
    }
}

@Composable
private fun Summary(it: DataState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = "Summary",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )
            Row(
                Modifier
                    .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = it.totalEntries.toString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Entries",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = it.totalMoods.toString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Moods",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = it.currentStreak.toString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Current Streak",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = it.longestStreak.toString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Longest Streak",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun HeatMapCalendar(
    heatMapData: Map<LocalDate, Level>,
    title: String,
    legendLeft: String,
    legendRight: String,
    legend: Array<Level>
) {
    val endDate = remember { LocalDate.now() }
    val startDate = remember { heatMapData.keys.last() }

    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            val heatMapCalendarState = rememberHeatMapCalendarState(
                startMonth = startDate.yearMonth,
                endMonth = endDate.yearMonth,
                firstVisibleMonth = endDate.yearMonth,
                firstDayOfWeek = firstDayOfWeekFromLocale(),
            )
            Text(
                text = title,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
            )
            HeatMapCalendar(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                state = heatMapCalendarState,
                contentPadding = PaddingValues(end = 6.dp),
                dayContent = { day, week ->
                    Day(
                        day = day,
                        startDate = startDate,
                        endDate = endDate,
                        week = week,
                        level = heatMapData[day.date] ?: Zero,
                    ) { clicked ->
                        Toast.makeText(context, clicked.toString(), Toast.LENGTH_SHORT).show()
                    }
                },
                monthHeader = { MonthHeader(it, endDate, heatMapCalendarState) },
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    legendLeft,
                    Modifier.padding(end = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
                legend.forEach { LevelBox(it.color) }
                Text(
                    legendRight,
                    Modifier.padding(start = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    startDate: LocalDate,
    endDate: LocalDate,
    week: HeatMapWeek,
    level: Level,
    onClick: (LocalDate) -> Unit,
) {
    val weekDates = week.days.map { it.date }
    if (day.date in startDate..endDate) {
        LevelBox(level.color) { onClick(day.date) }
    } else if (weekDates.contains(startDate)) {
        LevelBox(Zero.color)
    }
}

private val daySize = 18.dp

@Composable
private fun LevelBox(color: Color, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .size(daySize)
            .padding(2.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(color = color)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
    )
}

@Composable
private fun MonthHeader(
    calendarMonth: CalendarMonth,
    endDate: LocalDate,
    state: HeatMapCalendarState,
) {
    val density = LocalDensity.current
    val firstFullyVisibleMonth by remember {
        // Find the first index with at most one box out of bounds.
        derivedStateOf { getMonthWithYear(state.layoutInfo, density) }
    }
    if (calendarMonth.weekDays.first().first().date <= endDate) {
        val month = calendarMonth.yearMonth
        val title = if (month == firstFullyVisibleMonth) {
            month.displayText()
        } else {
            month.month.displayText()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp, start = 2.dp),
        ) {
            Text(text = title, fontSize = 10.sp)
        }
    }
}

private fun getMonthWithYear(
    layoutInfo: CalendarLayoutInfo,
    density: Density,
): YearMonth? {
    val visibleItemsInfo = layoutInfo.visibleMonthsInfo
    return when {
        visibleItemsInfo.isEmpty() -> null
        visibleItemsInfo.count() == 1 -> visibleItemsInfo.first().month.yearMonth
        else -> {
            val firstItem = visibleItemsInfo.first()
            val daySizePx = with(density) { daySize.toPx() }
            if (
                firstItem.size < daySizePx * 3 || // Ensure the Month + Year text can fit.
                firstItem.offset < layoutInfo.viewportStartOffset && // Ensure the week row size - 1 is visible.
                (layoutInfo.viewportStartOffset - firstItem.offset > daySizePx)
            ) {
                visibleItemsInfo[1].month.yearMonth
            } else {
                firstItem.month.yearMonth
            }
        }
    }
}

@Composable
fun DoughnutChart(
    values: List<MoodChartData>,
    size: Dp = 60.dp,
    thickness: Dp = 20.dp
) {

    val sumOfValues = values.map { it.value }.sum()
    val proportions = values.map { it.value }.map {
        it * 100 / sumOfValues
    }
    val sweepAngles = proportions.map {
        360 * it / 100
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = "Moods graph",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(start = 22.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .width(size)
                        .height(size + 10.dp)
                        .padding(top = 5.dp)
                ) {
                    Canvas(
                        modifier = Modifier.size(size = size)
                    ) {
                        var startAngle = -90f
                        for (i in values.indices) {
                            drawArc(
                                color = values[i].mood.mapToMoodIcon().color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngles[i],
                                useCenter = false,
                                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Butt)
                            )
                            startAngle += sweepAngles[i]
                        }
                    }
                }
                Legend(values)
            }
        }
    }
}

@Composable
fun Legend(values: List<MoodChartData>) {
    val sumOfValues = values.map { it.value }.sum()
    val proportions = values.map { it.value }.map {
        it * 100 / sumOfValues
    }
    Column(
        Modifier
            .padding(start = 28.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        values.forEachIndexed { i, mood ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(color = mood.mood.mapToMoodIcon().color, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = mood.mood.name.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            + " %.2f".format(proportions[i]) + "%" + " (${mood.value.toInt()})",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
