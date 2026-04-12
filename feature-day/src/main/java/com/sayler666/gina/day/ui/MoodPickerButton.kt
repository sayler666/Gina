package com.sayler666.gina.day.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.mood.ui.MoodIcon
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.ui.LocalHapticFeedbackManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun MoodButton(
    mood: Mood,
    showMoodPopup: MutableState<Boolean>,
    onMoodChanged: (Mood) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val moodIcon: MoodIcon = mood.mapToMoodIcon()

    var animationActive by remember { mutableStateOf(false) }
    val moodIconAnimParam by MoodIconAnimation().animateMoodIconAsState(
        mood = mood,
        active = animationActive
    )

    IconButton(onClick = { showMoodPopup.value = true }) {
        Icon(
            modifier = Modifier.scale(scale = moodIconAnimParam.scale),
            painter = rememberVectorPainter(image = moodIcon.icon),
            tint = moodIcon.color,
            contentDescription = null,
        )
    }
    MoodPicker(
        showMoodPopup.value,
        onDismiss = { showMoodPopup.value = false },
        onSelectMood = { selectedMood ->
            scope.launch {
                delay(60)
                showMoodPopup.value = false
            }
            animationActive = true
            onMoodChanged(selectedMood)
        }
    )
}

@Composable
private fun MoodPicker(showPopup: Boolean, onDismiss: () -> Unit, onSelectMood: (Mood) -> Unit) {
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
                modifier = Modifier.Companion.padding(16.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        3.dp
                    )
                )
            ) {
                val haptics = LocalHapticFeedbackManager.current
                Row {
                    Mood.valuesWithoutEmpty()
                        .forEach {
                            IconButton(
                                onClick = { haptics.tap(); onSelectMood(it) }
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

@Stable
data class MoodIconAnimParam(val scale: Float = 1f)

@Stable
class MoodIconAnimation(
    private val animationSpec: FiniteAnimationSpec<Float> = tween(250),
) {
    @Composable
    fun animateMoodIconAsState(mood: Mood?, active: Boolean): State<MoodIconAnimParam> {
        val fraction = remember { Animatable(0f) }

        LaunchedEffect(mood, active) {
            if (active) {
                fraction.snapTo(0f)
                fraction.animateTo(1f, animationSpec)
            }
        }

        return produceState(initialValue = MoodIconAnimParam(), key1 = fraction.value) {
            this.value = this.value.copy(scale = calculateScale(fraction.value))
        }
    }

    private fun calculateScale(fraction: Float): Float = sin(PI * fraction).toFloat() * 0.7f + 1
}
