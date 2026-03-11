package com.sayler666.gina.day.dayDetailsEdit.ui

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ripple
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import com.sayler666.core.file.handleMultipleVisualMedia
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.day.ui.MoodPicker
import com.sayler666.gina.friends.ui.FriendIcon
import com.sayler666.gina.friends.ui.FriendState
import com.sayler666.gina.friends.ui.FriendsPicker
import com.sayler666.gina.mood.ui.MoodIcon
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.DayDateHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun rememberLauncherForMultipleImages(
    context: Context,
    onResult: (List<Pair<ByteArray, String>>) -> Unit
) = rememberLauncherForActivityResult(PickMultipleVisualMedia()) {
    handleMultipleVisualMedia(it, context) { attachments ->
        onResult(attachments)
    }
}

@Composable
fun AttachmentsCountLabel(count: Int) {
    Text(
        text = stringResource(R.string.day_attachments_count, count),
        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary),
        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBar(
    dayOfMonth: String,
    dayOfWeek: String,
    yearAndMonth: String,
    hasWorkingCopy: Boolean,
    onNavigateBackClicked: () -> Unit,
    onChangeDateClicked: () -> Unit,
    onRestoreWorkingCopyClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onChangeDateClicked()
                }
            ) {
                DayDateHeader(
                    dayOfMonth = dayOfMonth,
                    dayOfWeek = dayOfWeek,
                    yearAndMonth = yearAndMonth
                )
                Icon(
                    Filled.ArrowDropDown,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            }
        },
        actions = {
            if (hasWorkingCopy) {
                IconButton(onClick = {
                    onRestoreWorkingCopyClicked()
                }) {
                    Icon(
                        rememberVectorPainter(image = Icons.AutoMirrored.Filled.Assignment),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { onNavigateBackClicked() }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        })
}

@Composable
fun SaveFab(onSaveButtonClicked: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.29f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            ),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        onClick = onSaveButtonClicked
    ) {
        Icon(
            Filled.Save, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TextFormat(showFormat: MutableState<Boolean>) {
    IconButton(onClick = { showFormat.value = !showFormat.value }) {
        Icon(Filled.TextFormat, null)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AttachmentsButton(onClick: () -> Unit, onLongClick: (() -> Unit)? = null) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false),
                enabled = true,
                onLongClick = { onLongClick?.invoke() },
                onClick = onClick
            )
            .padding(start = 8.dp, end = 8.dp)
    ) {
        Icon(Filled.AddAPhoto, null)
    }
}

@Composable
fun Friends(
    friends: List<FriendState>,
    allFriends: List<FriendState>,
    onSearchChanged: (String) -> Unit,
    onAddNewFriend: (String) -> Unit,
    onFriendClicked: (Int, Boolean) -> Unit,
) {
    val showFriendsPopup = remember { mutableStateOf(false) }

    when (friends.isNotEmpty()) {
        true -> Box(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .clickable(
                    indication = ripple(bounded = false),
                    interactionSource = remember {
                        MutableInteractionSource()
                    }) { showFriendsPopup.value = true }) {
            friends.take(2).forEachIndexed { i, friend ->
                FriendIcon(
                    friend = friend,
                    size = 32.dp,
                    modifier = Modifier
                        .offset(i * 8.dp)
                        .zIndex(-i.toFloat())
                )
            }
        }

        false -> IconButton(onClick = { showFriendsPopup.value = true }) {
            Icon(
                painter = rememberVectorPainter(image = Filled.People),
                contentDescription = null,
            )
        }
    }

    val searchQuery = rememberSaveable { mutableStateOf("") }
    FriendsPicker(
        showFriendsPopup.value,
        searchValue = searchQuery.value,
        onDismiss = { showFriendsPopup.value = false },
        onSearchChanged = {
            searchQuery.value = it
            onSearchChanged(it)
        },
        onAddNewFriend = {
            onAddNewFriend(it)
            searchQuery.value = ""
            onSearchChanged(searchQuery.value)
        },
        onFriendClicked = onFriendClicked,
        friends = allFriends
    )
}

@Composable
fun Mood(
    mood: Mood,
    showMoodPopup: MutableState<Boolean>,
    onMoodChanged: (Mood) -> Unit
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
            modifier = Modifier
                .scale(scale = moodIconAnimParam.scale),
            painter = rememberVectorPainter(image = moodIcon.icon),
            tint = moodIcon.color,
            contentDescription = null,
        )
    }
    MoodPicker(
        showMoodPopup.value,
        onDismiss = { showMoodPopup.value = false },
        onSelectMood = { mood ->
            scope.launch {
                delay(60)
                showMoodPopup.value = false
            }
            animationActive = true
            onMoodChanged(mood)
        })
}

@Stable
data class MoodIconAnimParam(
    val scale: Float = 1f,
)

@Stable
class MoodIconAnimation(
    private val animationSpec: FiniteAnimationSpec<Float> = tween(250)
) {
    @Composable
    fun animateMoodIconAsState(
        mood: Mood?,
        active: Boolean
    ): State<MoodIconAnimParam> {
        val fraction = remember { Animatable(0f) }

        LaunchedEffect(mood, active) {
            if (active) {
                fraction.snapTo(0f)
                fraction.animateTo(1f, animationSpec)
            }
        }

        return produceState(
            initialValue = MoodIconAnimParam(),
            key1 = fraction.value
        ) {
            this.value = this.value.copy(
                scale = calculateScale(fraction.value),
            )
        }
    }

    private fun calculateScale(
        fraction: Float,
    ): Float = sin(PI * fraction).toFloat() * 0.7f + 1
}
