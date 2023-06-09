package com.sayler666.gina.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.sayler666.gina.ui.Mood.AWESOME
import com.sayler666.gina.ui.Mood.BAD
import com.sayler666.gina.ui.Mood.GOOD
import com.sayler666.gina.ui.Mood.LOW
import com.sayler666.gina.ui.Mood.NEUTRAL
import com.sayler666.gina.ui.Mood.SUPERB

@Composable
fun MoodPicker(showPopup: Boolean, onDismiss: () -> Unit, onSelectMood: (Mood) -> Unit) {
    if (showPopup)
        Popup(
            offset = IntOffset(0, -160),
            onDismissRequest = { onDismiss() },
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(8.dp)
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

enum class Mood(val numberValue: Int) {
    EMPTY(Int.MIN_VALUE),
    BAD(-2),
    LOW(-1),
    NEUTRAL(0),
    GOOD(1),
    SUPERB(2),
    AWESOME(3);

    companion object {
        fun Int?.mapToMood() = when (this) {
            -2 -> BAD
            -1 -> LOW
            0 -> NEUTRAL
            1 -> GOOD
            2 -> SUPERB
            3 -> AWESOME
            else -> EMPTY
        }

        fun valuesWithoutEmpty() = Mood.values().toMutableList()
            .also { it.remove(EMPTY) }
    }
}

@Composable
fun Mood?.mapToMoodIcon(): MoodIcon = when (this) {
    BAD -> MoodIcon(
        icon = Filled.SentimentVeryDissatisfied,
        color = MaterialTheme.colorScheme.secondary
    )

    LOW -> MoodIcon(
        icon = Filled.SentimentDissatisfied,
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.75f)
    )

    NEUTRAL -> MoodIcon(
        icon = Filled.SentimentNeutral,
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    )

    GOOD -> MoodIcon(
        icon = Filled.SentimentSatisfied,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    )

    SUPERB -> MoodIcon(
        icon = Filled.SentimentVerySatisfied,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
    )

    AWESOME -> MoodIcon(
        icon = Filled.AutoAwesome,
        color = MaterialTheme.colorScheme.primary
    )

    else -> MoodIcon(
        icon = Outlined.Help,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

data class MoodIcon(
    val icon: ImageVector,
    val color: Color
)
