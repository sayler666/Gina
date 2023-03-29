package com.sayler666.gina.dayDetails.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key.Companion.VolumeDown
import androidx.compose.ui.input.key.Key.Companion.VolumeUp
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.core.file.Files
import com.sayler666.gina.core.flow.Event
import com.sayler666.gina.core.flow.withValue
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.NonImage
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel
import com.sayler666.gina.dayDetails.viewmodel.FriendEntity
import com.sayler666.gina.destinations.DayDetailsEditScreenDestination
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.FullImageDestination
import com.sayler666.gina.friends.ui.FriendIcon
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.mapToMoodIconOrNull
import kotlinx.coroutines.delay


data class DayDetailsScreenNavArgs(
    val dayId: Int
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@RootNavGraph
@Destination(navArgsDelegate = DayDetailsScreenNavArgs::class)
@Composable
fun DayDetailsScreen(
    destinationsNavigator: DestinationsNavigator,
    navController: NavController,
    viewModel: DayDetailsViewModel = hiltViewModel()
) {
    val day: DayDetailsEntity? by viewModel.day.collectAsStateWithLifecycle(null)
    val goToDay: Event<Int> by viewModel.goToDayId.collectAsStateWithLifecycle()
    goToDay.withValue { dayId ->
        destinationsNavigator.navigate(DayDetailsScreenDestination(DayDetailsScreenNavArgs(dayId))) {
            popUpTo(DayDetailsScreenDestination.route) { inclusive = true }
        }
    }
    val requester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    day?.let { day ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            DayTitle(day.dayOfMonth, day.dayOfWeek, day.yearAndMonth)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    day?.mood?.mapToMoodIconOrNull()?.let { icon ->
                        Icon(
                            rememberVectorPainter(image = icon.icon),
                            tint = icon.color,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    IconButton(onClick = {
                        day?.id?.let {
                            destinationsNavigator.navigate(DayDetailsEditScreenDestination(it))
                        }
                    }) {
                        Icon(Icons.Filled.Edit, null)
                    }
                })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                day?.let {
                    AttachmentsRow(it, destinationsNavigator)
                    Text(it)
                }
            }
        },
        bottomBar = {
            day?.friendsSelected?.let { friends ->
                if (friends.isNotEmpty())
                    BottomAppBar(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .wrapContentHeight(),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        content = { FriendsRow(friends) }
                    )
            }
        },
        modifier = Modifier
            .onKeyEvent {
                when (it.key) {
                    VolumeUp -> {
                        viewModel.goToNextDay()
                        return@onKeyEvent true
                    }
                    VolumeDown -> {
                        viewModel.goToPreviousDay()
                        return@onKeyEvent true
                    }
                    else -> return@onKeyEvent false
                }
            }
            .focusRequester(requester)
            .focusable()
    )

    LaunchedEffect(Unit) {
        delay(300)
        requester.requestFocus()
    }
}

@Composable
private fun Text(it: DayDetailsEntity) {
    Row(modifier = Modifier.padding(16.dp, 8.dp)) {
        Text(
            text = it.content,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AttachmentsRow(
    day: DayDetailsEntity,
    destinationsNavigator: DestinationsNavigator,
) {
    if (day.attachments.isNotEmpty()) {
        val context = LocalContext.current
        FlowRow(modifier = Modifier.padding(16.dp, 0.dp)) {
            day.attachments.forEach { attachment ->
                when (attachment) {
                    is Image -> ImagePreview(
                        attachment,
                        onClick = {
                            destinationsNavigator.navigate(
                                FullImageDestination(
                                    attachment.byte,
                                    attachment.mimeType
                                )
                            )
                        }
                    )
                    is NonImage -> FilePreview(
                        attachment,
                        onClick = {
                            Files.openFileIntent(
                                context,
                                attachment.byte,
                                attachment.mimeType
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FriendsRow(friends: List<FriendEntity>) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current
        friends.forEach { friend ->
            FriendIcon(friend = friend,
                size = 42.dp,
                modifier = Modifier
                    .padding(end = 8.dp, top = 0.dp)
                    .clickable(
                        indication = rememberRipple(bounded = false),
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        Toast
                            .makeText(context, friend.name, Toast.LENGTH_SHORT)
                            .show()
                    })
        }
    }
}
