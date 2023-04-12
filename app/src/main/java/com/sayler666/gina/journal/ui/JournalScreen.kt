package com.sayler666.gina.journal.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.R.string
import com.sayler666.gina.core.permission.Permissions
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.journal.viewmodel.DayEntity
import com.sayler666.gina.journal.viewmodel.JournalState
import com.sayler666.gina.journal.viewmodel.JournalState.DaysState
import com.sayler666.gina.journal.viewmodel.JournalState.EmptySearchState
import com.sayler666.gina.journal.viewmodel.JournalState.EmptyState
import com.sayler666.gina.journal.viewmodel.JournalState.PermissionNeededState
import com.sayler666.gina.journal.viewmodel.JournalViewModel
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.SearchBar
import com.sayler666.gina.ui.mapToMoodIconOrNull

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@RootNavGraph
@com.ramcosta.composedestinations.annotation.Destination
@Composable
fun JournalScreen(
    destinationsNavigator: DestinationsNavigator,
    navController: NavController
) {
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavGraphs.root.route)
    }
    val viewModel: JournalViewModel = hiltViewModel(backStackEntry)

    val permissionsResult = rememberLauncherForActivityResult(StartActivityForResult()) {
        viewModel.refreshPermissionStatus()
    }

    val searchText = rememberSaveable { mutableStateOf("") }
    val state: JournalState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SearchBar(
                title = "Gina",
                searchText = searchText.value,
                onSearchTextChanged = {
                    searchText.value = it
                    viewModel.searchQuery(searchText.value)
                },
                onClearClick = {
                    viewModel.searchQuery(null)
                    searchText.value = ""
                }
            )
        },
        content = { padding ->
            Journal(
                padding,
                destinationsNavigator,
                state,
                onPermissionClick = { permissionsResult.launch(Permissions.getManageAllFilesSettingsIntent()) }
            )
        })
}

@Composable
private fun Journal(
    padding: PaddingValues,
    destinationsNavigator: DestinationsNavigator,
    journalState: JournalState,
    onPermissionClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        when (journalState) {
            is DaysState -> Days(
                days = journalState.days,
                searchQuery = journalState.searchQuery,
                destinationsNavigator = destinationsNavigator
            )
            EmptySearchState -> EmptyResult(
                "Empty search result!",
                "Try narrowing search criteria."
            )
            EmptyState -> EmptyResult("No data found!", "Add some entries.")
            PermissionNeededState -> PermissionNeeded(onPermissionClick)
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun Days(
    days: List<DayEntity>,
    searchQuery: String? = null,
    destinationsNavigator: DestinationsNavigator
) {
    val grouped = days.groupBy { it.header }
    LazyColumn(contentPadding = PaddingValues(bottom = 34.dp)) {
        grouped.forEach { (header, day) ->
            stickyHeader {
                DateHeader(header)
            }

            items(day) { d ->
                Day(d, searchQuery) {
                    destinationsNavigator.navigate(
                        DayDetailsScreenDestination(
                            DayDetailsScreenNavArgs(d.id)
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Day(day: DayEntity, searchQuery: String? = null, onClick: () -> Unit) {
    Card(
        shape = RectangleShape,
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .6f),
        ),
        onClick = onClick
    ) {
        val icon = day.mood.mapToMoodIconOrNull()
        Column(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(Modifier.fillMaxWidth()) {
                DayTitle(day.dayOfMonth, day.dayOfWeek, day.yearAndMonth)
                icon?.let {
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
fun EmptyResult(header: String, body: String) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = header,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium
        )
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
            modifier = Modifier.padding(start = 8.dp, top = 5.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            text = header
        )
    }
}
