package com.sayler666.gina.insights.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.core.compose.plus
import com.sayler666.core.compose.scroll.rememberScrollConnection
import com.sayler666.core.compose.shimmerBrush
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.insights.viewmodel.ContributionLevel
import com.sayler666.gina.insights.viewmodel.InsightState
import com.sayler666.gina.insights.viewmodel.InsightState.DataState
import com.sayler666.gina.insights.viewmodel.InsightsViewModel
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent.OnLockBottomBar
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent.OnUnlockBottomBar
import com.sayler666.gina.insights.viewmodel.Level
import com.sayler666.gina.insights.viewmodel.MoodLevel
import com.sayler666.gina.mood.ui.awesomeColor
import com.sayler666.gina.mood.ui.badColor
import com.sayler666.gina.mood.ui.goodColor
import com.sayler666.gina.mood.ui.lowColor
import com.sayler666.gina.mood.ui.neutralColor
import com.sayler666.gina.mood.ui.superbColor
import com.sayler666.gina.ui.EmptyResult
import com.sayler666.gina.ui.FiltersBar
import com.sayler666.gina.ui.chart.MoodLineChart
import com.sayler666.gina.ui.hideNavBar.BOTTOM_NAV_HEIGHT
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import java.time.format.DateTimeFormatter

@Composable
fun InsightsScreen() {
    val viewModel: InsightsViewModel = hiltViewModel()
    val state: InsightState by viewModel.state.collectAsStateWithLifecycle()
    val moodsFilters: List<Mood> by viewModel.moodFilters.collectAsStateWithLifecycle()
    val filtersActive: Boolean by viewModel.filtersActive.collectAsStateWithLifecycle()

    Content(
        state = state,
        moodsFilters = moodsFilters,
        filtersActive = filtersActive,
        onViewEvent = viewModel::onViewEvent,
        onSearchQuery = viewModel::searchQuery,
        onMoodFiltersUpdate = viewModel::updateMoodFilters,
        onResetFilters = viewModel::resetFilters
    )
}

@Composable
private fun Content(
    state: InsightState,
    moodsFilters: List<Mood>,
    filtersActive: Boolean,
    onViewEvent: (ViewEvent) -> Unit,
    onSearchQuery: (String) -> Unit,
    onMoodFiltersUpdate: (List<Mood>) -> Unit,
    onResetFilters: () -> Unit
) {
    val hazeState = rememberHazeState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        InsightsContent(
            state = state,
            hazeState = hazeState,
            onViewEvent = onViewEvent
        )
        Toolbar(
            hazeState = hazeState,
            moodsFilters = moodsFilters,
            filtersActive = filtersActive,
            onViewEvent = onViewEvent,
            onSearchQuery = onSearchQuery,
            onMoodFiltersUpdate = onMoodFiltersUpdate,
            onResetFilters = onResetFilters
        )
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun Toolbar(
    hazeState: HazeState,
    moodsFilters: List<Mood>,
    filtersActive: Boolean,
    onViewEvent: (ViewEvent) -> Unit,
    onSearchQuery: (String) -> Unit,
    onMoodFiltersUpdate: (List<Mood>) -> Unit,
    onResetFilters: () -> Unit
) {
    val searchText = rememberSaveable { mutableStateOf("") }
    FiltersBar(
        modifier = Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                backgroundColor = colorScheme.background,
                tint = HazeTint(colorScheme.background.copy(alpha = 0.7f))
            )
        ) {
            progressive =
                HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
        },
        hazeState = hazeState,
        title = "Insights",
        searchText = searchText.value,
        onSearchTextChanged = {
            searchText.value = it
            onSearchQuery(it)
        },
        onClearClick = {
            searchText.value = ""
            onSearchQuery("")
        },
        moodFilters = moodsFilters,
        onMoodFiltersUpdate = onMoodFiltersUpdate,
        onResetFiltersClicked = onResetFilters,
        filtersActive = filtersActive,
        onSearchVisibilityChanged = { show ->
            when (show) {
                true -> onViewEvent(OnLockBottomBar)
                false -> onViewEvent(OnUnlockBottomBar)
            }
        }
    )
}

@Composable
private fun InsightsContent(
    modifier: Modifier = Modifier,
    state: InsightState,
    hazeState: HazeState,
    onViewEvent: (ViewEvent) -> Unit
) {
    val listState = rememberLazyListState()
    val nestedScrollConnection = rememberScrollConnection(
        onScrollDown = { onViewEvent(OnHideBottomBar) },
        onScrollUp = { onViewEvent(OnShowBottomBar) }
    )

    LaunchedEffect(!listState.canScrollForward) {
        onViewEvent(OnShowBottomBar)
    }
    Column(
        modifier
            .nestedScroll(nestedScrollConnection)
            .fillMaxSize()
            .imePadding()
            .hazeSource(hazeState),
    ) {
        when (state) {
            is DataState -> Insights(state)
            InsightState.EmptySearchState -> EmptyResult(
                "Empty search result!",
                "Try narrowing search criteria."
            )

            InsightState.EmptyState -> EmptyResult(
                "No data found!",
                "Add some entries."
            )

            InsightState.LoadingState -> {}
        }
        AnimatedVisibility(
            visible = state is InsightState.LoadingState,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Loading()
        }
    }
}

@Composable
private fun Loading() {
    Column(
        Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp),
        ) {
            Column(Modifier.padding(12.dp)) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .size(width = 160.dp, height = 27.dp)
                        .background(shimmerBrush(targetValue = 1100f))
                )

                Box(
                    modifier = Modifier
                        .padding()
                        .size(width = 800.dp, height = 45.dp)
                        .background(shimmerBrush(targetValue = 1600f))
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp),
        ) {
            Column(Modifier.padding(12.dp)) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .size(width = 160.dp, height = 27.dp)
                        .background(shimmerBrush(targetValue = 1100f))
                )

                Box(
                    modifier = Modifier
                        .padding()
                        .size(width = 800.dp, height = 165.dp)
                        .background(shimmerBrush(targetValue = 1600f))
                )
            }
        }
    }
}

@Composable
fun Insights(state: DataState) {
    val scrollState = rememberScrollState()
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 64.dp
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(Modifier.height(topPadding))
        Summary(state)

        Moods(state)

        FriendsList(state.friendsLastMonthStats, state.friendsAllTimeStats)

        Contribution(state)

        DoughnutChart(state.moodChartData)

        Spacer(
            modifier = Modifier.windowInsetsBottomHeight(
                WindowInsets.systemBars + WindowInsets(bottom = BOTTOM_NAV_HEIGHT + 12.dp)
            )
        )
    }
}

@Composable
fun Contribution(state: DataState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceColorAtElevation(3.dp)),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column {
            Text(
                text = "Contributions",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.labelLarge
                    .copy(color = colorScheme.onSurface)
            )
            HeatMapCalendar(
                modifier = Modifier.height(190.dp),
                state.contributionHeatMapData,
                "Less",
                "More",
                arrayOf<Level>(*ContributionLevel.entries.toTypedArray()).copyOfRange(
                    1,
                    ContributionLevel.entries.size
                ),
                colorProvider = { level -> contributionLevelColor(level as ContributionLevel) }
            )
        }
    }
}

enum class Mode {
    Day, Week, Month
}

@Composable
private fun Moods(state: DataState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceColorAtElevation(3.dp)),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(
            Modifier
                .animateContentSize()
                .padding(bottom = 8.dp)
        ) {

            val mode = remember { mutableStateOf(Mode.Month) }
            Row {
                Text(
                    text = "Moods",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.labelLarge
                        .copy(color = colorScheme.onSurface)
                )
                Spacer(modifier = Modifier.weight(1f))

                Row(
                    Modifier.padding(end = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        label = {
                            Text(
                                "By month",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        onClick = { mode.value = Mode.Month },
                        selected = mode.value == Mode.Month,
                        shape = RoundedCornerShape(8.dp),
                    )
                    FilterChip(
                        label = {
                            Text(
                                "By week",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        onClick = { mode.value = Mode.Week },
                        selected = mode.value == Mode.Week,
                        shape = RoundedCornerShape(8.dp),
                    )
                    FilterChip(
                        label = {
                            Text(
                                "By day",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        onClick = { mode.value = Mode.Day },
                        selected = mode.value == Mode.Day,
                        shape = RoundedCornerShape(8.dp),
                    )
                }
            }
            AnimatedContent(
                targetState = mode.value,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = ""
            ) { mode ->
                Row {
                    when (mode) {
                        Mode.Day -> HeatMapCalendar(
                            modifier = Modifier.height(190.dp),
                            state.moodHeatMapData,
                            "Worse",
                            "Better",
                            arrayOf<Level>(*MoodLevel.entries.toTypedArray()).copyOfRange(
                                1,
                                MoodLevel.entries.size
                            ),
                            colorProvider = { level -> moodLevelColor(level as MoodLevel) }
                        )

                        Mode.Week -> MoodLineChart(
                            modifier = Modifier.height(190.dp),
                            moods = state.moodByWeekData,
                            dateLabelFormatter = { date ->
                                date.format(DateTimeFormatter.ofPattern("d MMM\nyyyy"))
                            }
                        )

                        Mode.Month -> MoodLineChart(
                            modifier = Modifier.height(190.dp),
                            moods = state.moodByMonthData,
                            dateLabelFormatter = { date ->
                                date.format(DateTimeFormatter.ofPattern("MMM\nyyyy"))
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun zeroLevelColor() = colorScheme.onSurfaceVariant.copy(alpha = 0.1f)


@Composable
private fun contributionLevelColor(level: ContributionLevel): Color = when (level) {
    ContributionLevel.Zero -> zeroLevelColor()
    ContributionLevel.One -> colorScheme.tertiary.copy(alpha = 0.32f)
    ContributionLevel.Two -> colorScheme.tertiary.copy(alpha = 0.49f)
    ContributionLevel.Three -> colorScheme.tertiary.copy(alpha = 0.66f)
    ContributionLevel.Four -> colorScheme.tertiary.copy(alpha = 0.83f)
    ContributionLevel.Five -> colorScheme.tertiary
}

@Composable
private fun moodLevelColor(level: MoodLevel): Color = when (level) {
    MoodLevel.Zero -> zeroLevelColor()
    MoodLevel.Bad -> badColor()
    MoodLevel.Low -> lowColor()
    MoodLevel.Neutral -> neutralColor()
    MoodLevel.Good -> goodColor()
    MoodLevel.Superb -> superbColor()
    MoodLevel.Awesome -> awesomeColor()
}
