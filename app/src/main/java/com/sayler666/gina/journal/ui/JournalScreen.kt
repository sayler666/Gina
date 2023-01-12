package com.sayler666.gina.journal.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.NavGraphs
import com.sayler666.gina.dayDetails.ui.DayDetailsScreenNavArgs
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.journal.viewmodel.DayEntity
import com.sayler666.gina.journal.viewmodel.JournalViewModel
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.SearchBar
import com.sayler666.gina.ui.mapToMoodIconOrNull

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class
)
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
    val days: List<DayEntity> by viewModel.daysSearch.collectAsStateWithLifecycle()
    val searchText = rememberSaveable { mutableStateOf("") }

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
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Days(days, destinationsNavigator)
            }
        })
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun Days(
    days: List<DayEntity>,
    destinationsNavigator: DestinationsNavigator
) {
    val grouped = days.groupBy { it.header }
    LazyColumn {
        grouped.forEach { (header, day) ->
            stickyHeader {
                YearMonthHeader(header)
            }

            items(day) { d ->
                Day(d) {
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

@Composable
private fun YearMonthHeader(header: String) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Day(day: DayEntity, onClick: () -> Unit) {
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
                        tint = icon.tint,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = day.shortContent,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
