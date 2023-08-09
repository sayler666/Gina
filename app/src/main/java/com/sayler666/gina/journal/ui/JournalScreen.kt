package com.sayler666.gina.journal.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.compose.shimmerBrush
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.R.string
import com.sayler666.gina.core.permission.Permissions
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.ginaApp.viewModel.BottomNavigationBarViewModel
import com.sayler666.gina.journal.viewmodel.DayEntity
import com.sayler666.gina.journal.viewmodel.JournalState
import com.sayler666.gina.journal.viewmodel.JournalState.DaysState
import com.sayler666.gina.journal.viewmodel.JournalState.EmptySearchState
import com.sayler666.gina.journal.viewmodel.JournalState.EmptyState
import com.sayler666.gina.journal.viewmodel.JournalState.LoadingState
import com.sayler666.gina.journal.viewmodel.JournalState.PermissionNeededState
import com.sayler666.gina.journal.viewmodel.JournalViewModel
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.EmptyResult
import com.sayler666.gina.ui.FiltersBar
import mood.Mood
import mood.ui.mapToMoodIcon

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@RootNavGraph
@com.ramcosta.composedestinations.annotation.Destination
@Composable
fun JournalScreen(
    destinationsNavigator: DestinationsNavigator,
    navController: NavController,
    bottomBarViewModel: BottomNavigationBarViewModel
) {
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavGraphs.root.route)
    }
    val viewModel: JournalViewModel = hiltViewModel(backStackEntry)
    val permissionsResult = rememberLauncherForActivityResult(StartActivityForResult()) {
        viewModel.refreshPermissionStatus()
    }

    val state: JournalState by viewModel.state.collectAsStateWithLifecycle()
    val searchText = rememberSaveable { mutableStateOf("") }
    val moodsFilters: List<Mood> by viewModel.moodFilters.collectAsStateWithLifecycle()
    val filtersActive: Boolean by viewModel.filtersActive.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            FiltersBar(
                title = "Gina",
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
                        true -> bottomBarViewModel.lockHide()
                        false -> bottomBarViewModel.unlockAndShow()
                    }
                }
            )
        },
        content = { padding ->
            Journal(
                padding,
                destinationsNavigator,
                state,
                onPermissionClick = { permissionsResult.launch(Permissions.getManageAllFilesSettingsIntent()) },
                onScrollStarted = {
                    bottomBarViewModel.hide()
                },
                onScrollEnded = {
                    bottomBarViewModel.show()
                }
            )
        })
}

@Composable
private fun Journal(
    padding: PaddingValues,
    destinationsNavigator: DestinationsNavigator,
    state: JournalState,
    onPermissionClick: () -> Unit,
    onScrollStarted: () -> Unit,
    onScrollEnded: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        when (state) {
            is DaysState -> Days(
                days = state.days,
                searchQuery = state.searchQuery,
                destinationsNavigator = destinationsNavigator,
                onScrollStarted = {
                    onScrollStarted()
                },
                onScrollEnded = {
                    onScrollEnded()
                }
            )

            EmptySearchState -> EmptyResult(
                "Empty search result!",
                "Try narrowing search criteria."
            )

            EmptyState -> EmptyResult("No data found!", "Add some entries.")
            PermissionNeededState -> PermissionNeeded(onPermissionClick)
            LoadingState -> {}
        }
        AnimatedVisibility(
            visible = state is LoadingState,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Loading()
        }
    }
}

@Composable
fun Loading() {
    Column(Modifier.fillMaxSize()) {
        repeat(3) {
            Column(Modifier.padding(12.dp)) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .size(width = 140.dp, height = 30.dp)
                        .background(shimmerBrush(targetValue = 1100f))
                )

                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .size(width = 800.dp, height = 20.dp)
                        .background(shimmerBrush(targetValue = 1600f))
                )
                Box(
                    modifier = Modifier
                        .size(width = 800.dp, height = 20.dp)
                        .background(shimmerBrush(targetValue = 1600f))
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun Days(
    days: List<DayEntity>,
    searchQuery: String? = null,
    destinationsNavigator: DestinationsNavigator,
    onScrollStarted: () -> Unit,
    onScrollEnded: () -> Unit
) {
    val grouped = days.groupBy { it.header }
    val listState = rememberLazyListState()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta > 30) onScrollEnded()
                if (delta < -20) onScrollStarted()
                return Offset.Zero
            }
        }
    }
    LazyColumn(
        Modifier.nestedScroll(nestedScrollConnection),
        contentPadding = PaddingValues(bottom = 34.dp), state = listState
    ) {
        grouped.forEach { (header, days) ->
            stickyHeader {
                DateHeader(header)
            }

            itemsIndexed(
                items = days,
                key = { _, item -> item.id }
            ) { i, dayEntity ->
                Day(modifier = Modifier.animateItemPlacement(), dayEntity, searchQuery) {
                    destinationsNavigator.navigate(
                        DayDetailsScreenDestination(
                            DayDetailsScreenNavArgs(dayEntity.id)
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Day(
    modifier: Modifier = Modifier,
    day: DayEntity,
    searchQuery: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        onClick = onClick
    ) {
        val icon = day.mood.mapToMoodIcon()
        Column(
            Modifier
                .padding(start = 14.dp, end = 14.dp, bottom = 6.dp)
                .fillMaxWidth()
        ) {
            Row(Modifier.fillMaxWidth()) {
                DayTitle(day.dayOfMonth, day.dayOfWeek, day.yearAndMonth)
                icon.let {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        painter = rememberVectorPainter(
                            image = icon.icon
                        ),
                        tint = icon.color,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            val text = if (searchQuery == null) {
                buildAnnotatedString { append(day.shortContent) }
            } else {
                buildAnnotatedString {
                    val startIndex = day.shortContent.indexOf(searchQuery, ignoreCase = true)
                    val endIndex = startIndex + searchQuery.length
                    append(day.shortContent)
                    addStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            background = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        start = startIndex,
                        end = endIndex
                    )
                }
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PermissionNeeded(
    onClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            onClick = {
                onClick()
            },
        ) {
            Text(
                style = MaterialTheme.typography.labelLarge,
                text = stringResource(string.select_database_grant_permission)
            )
        }
    }
}

@Composable
private fun DateHeader(header: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(1f)
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
    ) {
        Text(
            modifier = Modifier.padding(start = 14.dp, top = 5.dp, bottom = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            text = header,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
