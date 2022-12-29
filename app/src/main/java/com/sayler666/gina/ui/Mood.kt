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
import com.sayler666.gina.daysList.viewmodel.Mood
import com.sayler666.gina.daysList.viewmodel.Mood.BAD
import com.sayler666.gina.daysList.viewmodel.Mood.GOOD
import com.sayler666.gina.daysList.viewmodel.Mood.LOW
import com.sayler666.gina.daysList.viewmodel.Mood.NEUTRAL
import com.sayler666.gina.daysList.viewmodel.Mood.SUPERB
import com.sayler666.gina.ui.theme.md_theme_dark_error
import com.sayler666.gina.ui.theme.md_theme_dark_errorContainer
import com.sayler666.gina.ui.theme.md_theme_dark_onSurfaceVariant
import com.sayler666.gina.ui.theme.md_theme_dark_onTertiaryContainer
import com.sayler666.gina.ui.theme.md_theme_dark_surfaceTint

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

fun Mood?.mapToMoodIcon(): MoodIcon = mapToMoodIconOrNull() ?: MoodIcon(
    icon = Filled.SentimentNeutral,
    tint = md_theme_dark_onSurfaceVariant
)

fun Mood?.mapToMoodIconOrNull(): MoodIcon? = when (this) {
    BAD -> MoodIcon(icon = Filled.SentimentVeryDissatisfied, tint = md_theme_dark_errorContainer)
    LOW -> MoodIcon(icon = Filled.SentimentDissatisfied, tint = md_theme_dark_error)
    NEUTRAL -> MoodIcon(icon = Filled.SentimentNeutral, tint = md_theme_dark_onSurfaceVariant)
    GOOD -> MoodIcon(icon = Filled.SentimentSatisfied, tint = md_theme_dark_surfaceTint)
    SUPERB -> MoodIcon(
        icon = Filled.SentimentVerySatisfied, tint = md_theme_dark_onTertiaryContainer
    )
    else -> null
}

data class MoodIcon(
    val icon: ImageVector,
    val tint: Color
)
