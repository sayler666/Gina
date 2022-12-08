package com.sayler666.gina.dayslist.ui

import android.annotation.SuppressLint
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.dayslist.viewmodel.DayEntity
import com.sayler666.gina.dayslist.viewmodel.DaysViewModel
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination
@OptIn(
    ExperimentalLifecycleComposeApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun DaysList(
    destinationsNavigator: DestinationsNavigator,
    viewModel: DaysViewModel = hiltViewModel()
) {
    val days: List<DayEntity> by viewModel.days.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                Toast.makeText(context, "Add new entry", LENGTH_SHORT).show()
            }) {
                Icon(Icons.TwoTone.Add, contentDescription = "Add new entry")
            }
        }, content = {
            Column(Modifier.fillMaxSize()) {
                val grouped = days.groupBy { it.header }
                LazyColumn(
                    contentPadding = PaddingValues(0.dp, 16.dp)
                ) {
                    grouped.forEach { (header, day) ->
                        stickyHeader {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
                            ) {
                                Text(
                                    modifier = Modifier.padding(16.dp, 8.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    text = header
                                )
                            }
                        }

                        items(day) { d ->
                            Day(d) {
                                Toast.makeText(context, "Clicked ID: ${d.id}", LENGTH_SHORT).show()
                            }
                        }
                    }

                }
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Day(day: DayEntity, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 8.dp),
        onClick = onClick
    ) {
        Column(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row {
                Text(
                    text = day.dayOfMonth,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(Modifier.padding(5.dp)) {
                    Text(
                        text = day.dayOfWeek,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = day.yearAndMonth,
                        style = MaterialTheme.typography.bodySmall,
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
