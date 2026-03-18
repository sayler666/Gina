package com.sayler666.gina.ui.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.sayler666.core.collections.mutate
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.mood.ui.mapToMoodIcon

@Composable
fun MoodFilter(
    moodFilters: List<Mood>,
    onSelectMood: (List<Mood>) -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Mood.entries.forEach { mood ->
            var checked by remember(moodFilters) { mutableStateOf(moodFilters.any { it == mood }) }
            IconToggleButton(
                checked = checked,
                onCheckedChange = {
                    checked = !checked
                    val newMoods =
                        moodFilters.mutate { if (checked) it.add(mood) else it.remove(mood) }
                    onSelectMood(newMoods)
                },
                colors = IconButtonDefaults.iconToggleButtonColors(),
            ) {
                val moodIcon = mood.mapToMoodIcon()
                Icon(
                    painter = rememberVectorPainter(image = moodIcon.icon),
                    tint = if (checked) moodIcon.color else MaterialTheme.colorScheme.outline,
                    contentDescription = null,
                )
            }
        }
    }
}
