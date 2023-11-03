package com.sayler666.gina.mood.ui

import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.sayler666.gina.mood.Mood
import com.sayler666.gina.mood.Mood.AWESOME
import com.sayler666.gina.mood.Mood.BAD
import com.sayler666.gina.mood.Mood.GOOD
import com.sayler666.gina.mood.Mood.LOW
import com.sayler666.gina.mood.Mood.NEUTRAL
import com.sayler666.gina.mood.Mood.SUPERB

data class MoodIcon(
    val icon: ImageVector,
    val color: Color
)

@Composable
fun Mood?.mapToMoodIcon(): MoodIcon = when (this) {
    BAD -> MoodIcon(
        icon = Filled.SentimentVeryDissatisfied,
        color = badColor()
    )

    LOW -> MoodIcon(
        icon = Filled.SentimentDissatisfied,
        color = lowColor()
    )

    NEUTRAL -> MoodIcon(
        icon = Filled.SentimentNeutral,
        color = neutralColor()
    )

    GOOD -> MoodIcon(
        icon = Filled.SentimentSatisfied,
        color = goodColor()
    )

    SUPERB -> MoodIcon(
        icon = Filled.SentimentVerySatisfied,
        color = superbColor()
    )

    AWESOME -> MoodIcon(
        icon = Filled.AutoAwesome,
        color = awesomeColor()
    )

    else -> MoodIcon(
        icon = Outlined.Help,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun badColor() = MaterialTheme.colorScheme.secondary

@Composable
fun lowColor() = MaterialTheme.colorScheme.secondary.copy(alpha = 0.75f)

@Composable
fun neutralColor() = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)

@Composable
fun goodColor() = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)

@Composable
fun superbColor() = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)

@Composable
fun awesomeColor() = MaterialTheme.colorScheme.primary
