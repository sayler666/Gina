package com.sayler666.gina.feature.journal.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sayler666.gina.ui.theme.GinaTheme
import com.sayler666.gina.ui.theme.Theme
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
                    top = 8.dp,
                    bottom = 6.dp
                )
                .fillMaxWidth(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun ListStickyHeaderPreview() {
    GinaTheme(Theme.Firewatch, darkTheme = true) {
        ListStickyHeader(text = "2026, March", hazeState = HazeState())
    }
}
