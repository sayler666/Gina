package com.sayler666.gina.feature.journal.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil.compose.rememberAsyncImagePainter
import com.sayler666.core.flow.throttleFirst
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.ui.CaesarCipherText
import com.sayler666.gina.ui.DayDateHeader
import com.sayler666.gina.ui.LocalSharedTransitionScope
import com.sayler666.gina.ui.theme.GinaTheme
import com.sayler666.gina.ui.theme.Theme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.ByteArrayOutputStream
import kotlin.math.PI
import kotlin.math.sin

data class DayRowState(
    val id: Int,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val header: String,
    val shortContent: String,
    val searchQuery: String,
    val mood: Mood? = null,
    val displayAttachmentIds: ImmutableList<Int> = persistentListOf(),
    val allAttachmentIds: ImmutableList<Int> = persistentListOf()
)

@OptIn(FlowPreview::class)
@Composable
fun DayRow(
    modifier: Modifier = Modifier,
    state: DayRowState,
    onClick: () -> Unit,
    onSwipeToEdit: () -> Unit = {},
    onAttachmentClick: (attachmentId: Int, allIds: List<Int>) -> Unit = { _, _ -> },
    loadImage: suspend (Int) -> ByteArray? = { null },
    incognitoMode: Boolean = false,
    top: Boolean = false,
    bottom: Boolean = false
) {
    val shape = calculateShape(top = top, bottom = bottom)

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * 0.30f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            SwipeToEditBackground(
                dismissState = dismissState,
                shape = shape,
                onSwipeToEdit = onSwipeToEdit
            )
        }
    ) {
        DayRowContent(
            shape = shape,
            onClick = onClick,
            state = state,
            incognitoMode = incognitoMode,
            onAttachmentClick = onAttachmentClick,
            loadImage = loadImage
        )
    }
}

@Composable
private fun calculateShape(
    top: Boolean,
    bottom: Boolean
): RoundedCornerShape {
    val density = LocalDensity.current
    val corner = with(density) {
        MaterialTheme.shapes.large.topStart.toPx(Size.Unspecified, this).toDp()
    }
    val shape = when {
        top && bottom -> RoundedCornerShape(
            topStart = corner,
            topEnd = corner,
            bottomStart = corner,
            bottomEnd = corner
        )

        top -> RoundedCornerShape(topStart = corner, topEnd = corner)
        bottom -> RoundedCornerShape(bottomStart = corner, bottomEnd = corner)
        else -> RoundedCornerShape(0.dp)
    }
    return shape
}

@Composable
private fun DayRowContent(
    shape: RoundedCornerShape,
    onClick: () -> Unit,
    state: DayRowState,
    incognitoMode: Boolean,
    onAttachmentClick: (Int, List<Int>) -> Unit,
    loadImage: suspend (Int) -> ByteArray?
) {
    Card(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.4.dp),
        ),
        shape = shape,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            when (state.displayAttachmentIds.size) {
                0 -> NoAttachmentLayout(state, incognitoMode)
                1 -> OneAttachmentLayout(state, incognitoMode, onAttachmentClick, loadImage)
                else -> ManyAttachmentsLayout(
                    state,
                    incognitoMode,
                    onAttachmentClick,
                    loadImage
                )
            }
        }
    }
}

@Composable
private fun SwipeToEditBackground(
    dismissState: SwipeToDismissBoxState,
    shape: RoundedCornerShape,
    onSwipeToEdit: () -> Unit = {},
) {
    val scaleAnimatable = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        snapshotFlow { dismissState.currentValue }
            .throttleFirst(200L)
            .collect { value ->
                if (value == SwipeToDismissBoxValue.StartToEnd) {
                    onSwipeToEdit()
                    scaleAnimatable.animateTo(2f, animationSpec = TweenSpec(easing = EaseOutBounce))
                    delay(200)
                    scaleAnimatable.snapTo(0f)
                    dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                }
            }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val maxWidth = constraints.maxWidth.toFloat() * 0.5f
        val offset = runCatching { dismissState.requireOffset() }.getOrDefault(0f)
        val alpha = (offset / maxWidth).coerceIn(0f, 1f)
        val color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = alpha)
        Timber.d("offset: $offset, alpha: $alpha")
        Box(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 1.dp)
                .clip(shape)
                .background(color),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = ((1 + alpha) * 20).dp)
                    .size(36.dp)
                    .scale(1f + scaleAnimatable.value)
                    .graphicsLayer {
                        rotationZ = getOscillatedValue(alpha)
                        transformOrigin = TransformOrigin(0.2f, 0.8f)
                    },
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

fun getOscillatedValue(input: Float): Float {
    val start = 0.4f
    val end = 1f
    val minVal = -15f
    val maxVal = 20f

    if (input !in start..end) {
        return (maxVal + minVal) / 2f
    }

    val normalizedInput = (input - start) / (end - start)

    val cycles = 5
    val range = maxVal - minVal

    val sineValue = sin(2 * PI * cycles * normalizedInput.toDouble()).toFloat()
    val normalizedSine = (sineValue + 1f) / 2f

    return (normalizedSine * range) + minVal
}

@Composable
private fun NoAttachmentLayout(state: DayRowState, incognitoMode: Boolean) {
    Box {
        Column(
            Modifier
                .fillMaxWidth()
                .commonContentPadding()
        ) {
            DateMoodRow(state)
            ContentText(state, incognitoMode)
        }
        MoodIcon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp),
            state = state
        )
    }
}

@Composable
private fun OneAttachmentLayout(
    state: DayRowState,
    incognitoMode: Boolean,
    onAttachmentClick: (Int, List<Int>) -> Unit,
    loadImage: suspend (Int) -> ByteArray?
) {
    val attachmentId = state.displayAttachmentIds.first()
    val cardColor = MaterialTheme.colorScheme.surfaceContainer
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.matchParentSize()) {
            Image(
                attachmentId = attachmentId,
                loadImage = loadImage,
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .clickable { onAttachmentClick(attachmentId, state.allAttachmentIds) }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(cardColor, cardColor.copy(alpha = 0f))
                        )
                    )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .commonContentPadding()
        ) {
            DateMoodRow(state)
            ContentText(state, incognitoMode)
        }
        MoodIcon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp),
            state = state
        )
    }
}


@Composable
private fun ManyAttachmentsLayout(
    state: DayRowState,
    incognitoMode: Boolean,
    onAttachmentClick: (Int, List<Int>) -> Unit,
    loadImage: suspend (Int) -> ByteArray?
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            state.displayAttachmentIds.forEachIndexed { index, attachmentId ->
                Image(
                    attachmentId = attachmentId,
                    loadImage = loadImage,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onAttachmentClick(attachmentId, state.allAttachmentIds) }
                )
                if (index < state.displayAttachmentIds.size - 1) Spacer(modifier = Modifier.width(1.dp))
            }
        }
        Box {
            Column(
                modifier = Modifier
                    .commonContentPadding()
            ) {
                DateMoodRow(state)
                ContentText(state, incognitoMode)
            }
            MoodIcon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp),
                state = state
            )
        }
    }
}

private fun Modifier.commonContentPadding(): Modifier = this
    .padding(horizontal = 12.dp)
    .padding(top = 6.dp, bottom = 14.dp)

@Composable
private fun ContentText(state: DayRowState, incognitoMode: Boolean) {
    val text = if (state.searchQuery.isEmpty()) {
        buildAnnotatedString { append(state.shortContent) }
    } else {
        buildAnnotatedString {
            val startIndex = state.shortContent.indexOf(state.searchQuery, ignoreCase = true)
            val endIndex = startIndex + state.searchQuery.length
            append(state.shortContent)
            if (startIndex >= 0) {
                addStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        background = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    start = startIndex,
                    end = endIndex
                )
            }
        }
    }
    if (incognitoMode) {
        CaesarCipherText(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DateMoodRow(state: DayRowState) {
    DayDateHeader(
        dayOfMonth = state.dayOfMonth,
        dayOfWeek = state.dayOfWeek,
        yearAndMonth = state.yearAndMonth
    )
}

@Composable
private fun MoodIcon(modifier: Modifier, state: DayRowState) {
    val icon = state.mood.mapToMoodIcon()
    Icon(
        modifier = modifier.size(20.dp),
        painter = rememberVectorPainter(image = icon.icon),
        tint = icon.color,
        contentDescription = null,
    )
}

@Composable
private fun Image(
    attachmentId: Int,
    loadImage: suspend (Int) -> ByteArray?,
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current
    val bytes by if (isPreview) {
        remember(attachmentId) { mutableStateOf(runBlocking { loadImage(attachmentId) }) }
    } else {
        produceState(null, attachmentId) {
            value = loadImage(attachmentId)
        }
    }
    if (bytes != null) {
        val sharedScope = LocalSharedTransitionScope.current
        val imageModifier: Modifier = if (sharedScope != null) {
            val sharedState = sharedScope.rememberSharedContentState("attachment_${attachmentId}")
            val animScope = LocalNavAnimatedContentScope.current
            with(sharedScope) {
                modifier
                    .sharedElement(
                        sharedContentState = sharedState,
                        animatedVisibilityScope = animScope
                    )
            }
        } else modifier

        val painter = if (isPreview) {
            BitmapPainter(BitmapFactory.decodeByteArray(bytes, 0, bytes!!.size).asImageBitmap())
        } else {
            rememberAsyncImagePainter(bytes)
        }
        Image(
            modifier = imageModifier,
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant))
    }
}

private val previewState = DayRowState(
    id = 1,
    dayOfMonth = "14",
    dayOfWeek = "Mon",
    yearAndMonth = "March 2025",
    header = "Great day",
    shortContent = "Today was a really productive day. Went for a walk in the morning and finished the new feature.  Went for a walk in the morning and finished the new feature.",
    searchQuery = "",
    mood = Mood.GOOD
)

private fun previewImageBytes(context: android.content.Context): ByteArray {
    val original = BitmapFactory.decodeResource(
        context.resources,
        com.sayler666.gina.resources.R.drawable.sample
    )
    val scaled = original.scale(200, 150)
    return ByteArrayOutputStream().also { scaled.compress(Bitmap.CompressFormat.JPEG, 80, it) }
        .toByteArray()
}

@PreviewLightDark
@Composable
private fun DayRowNoAttachmentsPreview() {
    GinaTheme(Theme.Firewatch) {
        val context = LocalContext.current
        Surface {
            Column(Modifier.padding(vertical = 8.dp)) {
                DayRow(
                    state = previewState,
                    onClick = {},
                    top = true,
                    bottom = false
                )
                DayRow(
                    state = previewState.copy(
                        displayAttachmentIds = persistentListOf(1),
                        allAttachmentIds = persistentListOf(1)
                    ),
                    onClick = {},
                    loadImage = { previewImageBytes(context) }
                )
                DayRow(
                    state = previewState.copy(
                        displayAttachmentIds = persistentListOf(1, 2),
                        allAttachmentIds = persistentListOf(1, 2)
                    ),
                    onClick = {},
                    loadImage = { previewImageBytes(context) }
                )
                DayRow(
                    state = previewState.copy(mood = null),
                    onClick = {},
                    bottom = true
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayRowManyAttachmentsPreview() {
    val context = LocalContext.current
    GinaTheme(Theme.Firewatch, darkTheme = true) {
        Surface {
            DayRow(
                state = previewState.copy(
                    displayAttachmentIds = persistentListOf(1, 2, 3),
                    allAttachmentIds = persistentListOf(1, 2, 3)
                ),
                onClick = {},
                loadImage = { previewImageBytes(context) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayRowSearchHighlightPreview() {
    GinaTheme(Theme.Firewatch, darkTheme = true) {
        Surface {
            DayRow(
                state = previewState.copy(searchQuery = "productive"),
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayRowIncognitoPreview() {
    GinaTheme(Theme.Firewatch, darkTheme = true) {
        Surface {
            DayRow(
                state = previewState,
                onClick = {},
                incognitoMode = true
            )
        }
    }
}
