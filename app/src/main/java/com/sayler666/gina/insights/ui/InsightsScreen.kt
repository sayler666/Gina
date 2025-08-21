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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.sayler666.core.compose.plus
import com.sayler666.core.compose.shimmerBrush
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.ginaApp.BOTTOM_NAV_HEIGHT
import com.sayler666.gina.insights.viewmodel.ContributionLevel
import com.sayler666.gina.insights.viewmodel.InsightState
import com.sayler666.gina.insights.viewmodel.InsightState.DataState
import com.sayler666.gina.insights.viewmodel.InsightsViewModel
import com.sayler666.gina.insights.viewmodel.InsightsViewModel.ViewEvent.OnLockBottomBar
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
import java.time.format.DateTimeFormatter

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class
)
@Destination
@Composable
fun InsightsScreen() {
    val viewModel: InsightsViewModel = hiltViewModel()
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
                filtersActive,
                onSearchVisibilityChanged = { show ->
                    when (show) {
                        true -> viewModel.onViewEvent(OnLockBottomBar)
                        false -> viewModel.onViewEvent(OnUnlockBottomBar)
                    }
                }
            )
        },
        content = { padding ->
            InsightsContent(
                Modifier.padding(top = padding.calculateTopPadding()),
                state
            )
        })
}

@Composable
private fun InsightsContent(
    modifier: Modifier = Modifier,
    state: InsightState
) {
    Column(
        modifier
            .fillMaxSize()
            .imePadding()
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
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Summary(state)

        Moods(state)

        FriendsList(state.friendsLastMonthStats, state.friendsAllTimeStats)

        Contribution(state)

        DoughnutChart(state.moodChartData)

        Spacer(
            modifier = Modifier.windowInsetsBottomHeight(
                WindowInsets.systemBars + WindowInsets(bottom = BOTTOM_NAV_HEIGHT)
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
                style = MaterialTheme.typography.titleMedium
                    .copy(color = colorScheme.onPrimaryContainer),
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
                    style = MaterialTheme.typography.titleMedium
                        .copy(color = colorScheme.onPrimaryContainer),
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
fun emptyLevelColor() = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

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
