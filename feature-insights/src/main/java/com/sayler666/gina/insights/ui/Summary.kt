package com.sayler666.gina.insights.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sayler666.gina.insights.viewmodel.InsightState
import com.sayler666.gina.resources.R

@Composable
fun Summary(it: InsightState.DataState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column {
            Text(
                text = stringResource(R.string.insights_summary_title),
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.labelMedium
                    .copy(color = MaterialTheme.colorScheme.onSurface)
            )
            Row(
                Modifier
                    .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedContent(
                        targetState = it.totalEntries.toString(),
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = ""
                    ) {
                        Text(
                            modifier = Modifier.animateEnterExit(
                                enter = scaleIn(),
                                exit = scaleOut()
                            ),
                            text = it,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Text(
                        text = stringResource(R.string.insights_summary_entries),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedContent(
                        targetState = it.totalMoods.toString(),
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = ""
                    ) {
                        Text(
                            modifier = Modifier.animateEnterExit(
                                enter = scaleIn(),
                                exit = scaleOut()
                            ),
                            text = it,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Text(
                        text = stringResource(R.string.insights_moods_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedContent(
                        targetState = it.currentStreak.toString(),
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = ""
                    ) {
                        Text(
                            modifier = Modifier.animateEnterExit(
                                enter = scaleIn(),
                                exit = scaleOut()
                            ),
                            text = it,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Text(
                        text = stringResource(R.string.insights_summary_current_streak),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedContent(
                        targetState = it.longestStreak.toString(),
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = ""
                    ) {
                        Text(
                            modifier = Modifier.animateEnterExit(
                                enter = scaleIn(),
                                exit = scaleOut()
                            ),
                            text = it,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Text(
                        text = stringResource(R.string.insights_summary_longest_streak),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
