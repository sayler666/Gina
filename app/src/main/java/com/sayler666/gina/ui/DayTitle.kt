package com.sayler666.gina.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sayler666.gina.ui.theme.RobotoSlabLight
import com.sayler666.gina.ui.theme.RobotoSlabRegular

@Composable
fun DayTitle(dayOfMonth: String, dayOfWeek: String, yearAndMonth: String) {
    Text(
        text = dayOfMonth,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.primary
    )
    Column(Modifier.padding(5.dp)) {
        Text(
            text = dayOfWeek,
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = RobotoSlabRegular),
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = yearAndMonth,
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = RobotoSlabLight),
        )
    }
}
