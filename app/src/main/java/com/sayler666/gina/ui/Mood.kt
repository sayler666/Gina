package com.sayler666.gina.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
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
import com.sayler666.gina.ui.Mood.BAD
import com.sayler666.gina.ui.Mood.GOOD
import com.sayler666.gina.ui.Mood.LOW
import com.sayler666.gina.ui.Mood.NEUTRAL
import com.sayler666.gina.ui.Mood.SUPERB
import com.sayler666.gina.ui.theme.md_theme_dark_onSurfaceVariant

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
                    Mood.values().reversed().forEach {
                        IconButton(
                            onClick = { onSelectMood(it) }
                        ) {
                            val icon = it.mapToMoodIcon()
                            Icon(
                                painter = rememberVectorPainter(image = icon.icon),
                                tint = icon.tint,
                                contentDescription = null,
                            )
                        }
                    }
                }

            }
        }

}

enum class Mood(val numberValue: Int) {

    BAD(-2),
    LOW(-1),
    NEUTRAL(0),
    GOOD(1),
    SUPERB(2);

    companion object {
        fun Int?.mapToMoodOrNull() = when (this) {
            -2 -> BAD
            -1 -> LOW
            0 -> NEUTRAL
            1 -> GOOD
            2 -> SUPERB
            else -> null
        }
    }
}

fun Mood?.mapToMoodIcon(): MoodIcon = mapToMoodIconOrNull() ?: MoodIcon(
    icon = Filled.SentimentNeutral,
    tint = md_theme_dark_onSurfaceVariant
)

fun Mood?.mapToMoodIconOrNull(): MoodIcon? = when (this) {
    BAD -> MoodIcon(
        icon = Filled.SentimentVeryDissatisfied,
        tint = Color(0xFFFF0000)
    )
    LOW -> MoodIcon(
        icon = Filled.SentimentDissatisfied,
        tint = Color(0xFFD36E2A)
    )
    NEUTRAL -> MoodIcon(
        icon = Filled.SentimentNeutral,
        tint = Color(0xFFFFFFFF)
    )
    GOOD -> MoodIcon(
        icon = Filled.SentimentSatisfied,
        tint = Color(0xFFE3E91E)
    )
    SUPERB -> MoodIcon(
        icon = Filled.SentimentVerySatisfied,
        tint = Color(0xFF1CCF24)
    )
    else -> null
}

data class MoodIcon(
    val icon: ImageVector,
    val tint: Color
)
