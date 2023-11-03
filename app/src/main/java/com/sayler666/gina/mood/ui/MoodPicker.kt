package com.sayler666.gina.mood.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.sayler666.gina.mood.Mood

@Composable
fun MoodPicker(showPopup: Boolean, onDismiss: () -> Unit, onSelectMood: (Mood) -> Unit) {
    Popup(
        offset = IntOffset(0, -160),
        onDismissRequest = { onDismiss() },
    ) {
        AnimatedVisibility(
            visible = showPopup,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row {
                    Mood.valuesWithoutEmpty()
                        .forEach {
                            IconButton(
                                onClick = { onSelectMood(it) }
                            ) {
                                val icon = it.mapToMoodIcon()
                                Icon(
                                    painter = rememberVectorPainter(image = icon.icon),
                                    tint = icon.color,
                                    contentDescription = null,
                                )
                            }
                        }
                }
            }
        }
    }
}
