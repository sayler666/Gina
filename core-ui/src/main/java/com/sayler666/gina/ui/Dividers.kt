package com.sayler666.gina.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerticalDivider() {
    Spacer(Modifier.width(8.dp))
    Divider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .padding(0.dp, 8.dp)
    )
    Spacer(Modifier.width(8.dp))
}
