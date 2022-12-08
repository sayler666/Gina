package com.sayler666.gina.dayslist.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.dayslist.viewmodel.DaysViewModel
import com.sayler666.gina.db.Days

@Destination
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun Days(destinationsNavigator: DestinationsNavigator, viewModel: DaysViewModel = hiltViewModel()) {
    val days: List<Days> by viewModel.days.collectAsStateWithLifecycle()

    Row(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Records: ${days.size}",
            fontSize = 16.sp
        )
    }
}
