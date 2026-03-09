package com.sayler666.gina.feature.journal.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun ListStickyHeader(
    text: String,
    hazeState: HazeState,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .hazeSource(hazeState)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(
                    start = 14.dp,
                    top = 5.dp,
                    bottom = 8.dp
                )
                .fillMaxWidth(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
