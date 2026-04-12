package com.sayler666.gina.day.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.sayler666.gina.ui.DayDateHeader

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DayTopBar(
    dayOfMonth: String,
    dayOfWeek: String,
    yearAndMonth: String,
    hasWorkingCopy: Boolean,
    onNavigateBackClicked: () -> Unit,
    onChangeDateClicked: () -> Unit,
    onRestoreWorkingCopyClicked: () -> Unit,
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onChangeDateClicked() }
            ) {
                DayDateHeader(
                    dayOfMonth = dayOfMonth,
                    dayOfWeek = dayOfWeek,
                    yearAndMonth = yearAndMonth
                )
                Icon(
                    Filled.ArrowDropDown,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            }
        },
        actions = {
            if (hasWorkingCopy) {
                IconButton(onClick = { onRestoreWorkingCopyClicked() }) {
                    Icon(
                        rememberVectorPainter(image = Icons.AutoMirrored.Filled.Assignment),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { onNavigateBackClicked() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        }
    )
}
