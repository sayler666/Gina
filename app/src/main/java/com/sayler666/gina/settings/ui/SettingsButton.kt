package com.sayler666.gina.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsButton(
    header: String,
    body: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .clip(shape = CircleShape)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Icon(icon, null, Modifier.padding(10.dp))
            }
            Column(modifier = Modifier.padding(0.dp)) {
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelLarge
                        .copy(color = MaterialTheme.colorScheme.onBackground)
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.labelMedium
                        .copy(color = MaterialTheme.colorScheme.outline)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Filled.ChevronRight, null, Modifier.padding(end = 8.dp))
        }
    }
}
