package com.sayler666.gina.insights.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sayler666.gina.friends.ui.FriendIcon
import com.sayler666.gina.friends.viewmodel.FriendEntity
import com.sayler666.gina.ui.EmptyResult

@Composable
fun FriendsList(
    friends: List<FriendEntity>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(
            Modifier
                .animateContentSize()
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = "Friends",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
            )
            if (friends.isNotEmpty()) {
                FriendsChart(friends)
            } else {
                EmptyResult(
                    "No data found!",
                    "No friends found within given filters.",
                    headerStyle = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
private fun FriendsChart(
    friends: List<FriendEntity>
) {
    var friendsToShow by remember { mutableIntStateOf(5) }
    val maxCount = friends.first().daysCount
    friends.take(friendsToShow).onEachIndexed { index, friend ->
        FriendBar(friend, index + 1, maxCount)
    }
    if (friends.count() > friendsToShow) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = { friendsToShow += 5 }) {
                Text(text = "Show more", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun FriendBar(friend: FriendEntity, position: Int, maxCount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(40.dp)
            .padding(end = 12.dp)
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(20.dp),
            text = "$position.",
            style = MaterialTheme.typography.bodyMedium,
        )

        AnimatedContent(
            targetState = friend,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = ""
        ) {
            FriendIcon(it, size = 32.dp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f, true),
            contentAlignment = Alignment.CenterStart,
        ) {
            AnimatedContent(
                targetState = friend.name,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = ""
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .animateContentSize()
                    .fillMaxWidth(friend.daysCount / maxCount.toFloat())
                    .background(MaterialTheme.colorScheme.primary)
                    .height(23.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                AnimatedContent(
                    targetState = friend.name,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = ""
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.surface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        AnimatedContent(
            targetState = friend.daysCount,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = ""
        ) {
            Text(
                text = "$it",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )
        }
    }
}
