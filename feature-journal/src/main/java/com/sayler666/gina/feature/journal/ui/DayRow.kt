package com.sayler666.gina.feature.journal.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.ui.CaesarCipherText
import com.sayler666.gina.ui.DayDateHeader
import com.sayler666.gina.ui.LocalSharedTransitionScope

data class DayRowState(
    val id: Int,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val header: String,
    val shortContent: String,
    val searchQuery: String,
    val mood: Mood? = null
)

@Composable
fun DayRow(
    modifier: Modifier = Modifier,
    state: DayRowState,
    onClick: () -> Unit,
    incognitoMode: Boolean = false
) {
    val sharedScope = LocalSharedTransitionScope.current

    Card(
        modifier = modifier,
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        onClick = onClick
    ) {
        val icon = state.mood.mapToMoodIcon()
        Column(
            Modifier
                .padding(start = 14.dp, end = 14.dp, bottom = 6.dp)
                .fillMaxWidth()
        ) {
            Row(Modifier.fillMaxWidth()) {

                val sharedModifier = if (sharedScope != null) {
                    val sharedState =
                        sharedScope.rememberSharedContentState("dayDateHeader_${state.id}")
                    with(sharedScope) {
                        Modifier
                            .sharedElement(
                                sharedContentState = sharedState,
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            )
                    }
                } else {
                    Modifier
                }

                DayDateHeader(
                    modifier = sharedModifier,
                    dayOfMonth = state.dayOfMonth,
                    dayOfWeek = state.dayOfWeek,
                    yearAndMonth = state.yearAndMonth
                )
                icon.let {
                    val sharedModifier = if (sharedScope != null) {
                        val sharedState =
                            sharedScope.rememberSharedContentState("mood_${state.id}")
                        with(sharedScope) {
                            Modifier
                                .size(18.dp)
                                .sharedElement(
                                    sharedContentState = sharedState,
                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                )
                        }
                    } else {
                        Modifier.size(18.dp)
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        modifier = sharedModifier,
                        painter = rememberVectorPainter(image = icon.icon),
                        tint = icon.color,
                        contentDescription = null,
                    )
                }
            }
            val text = if (state.searchQuery.isEmpty()) {
                buildAnnotatedString { append(state.shortContent) }
            } else {
                buildAnnotatedString {
                    val startIndex =
                        state.shortContent.indexOf(state.searchQuery, ignoreCase = true)
                    val endIndex = startIndex + state.searchQuery.length
                    append(state.shortContent)
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
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
