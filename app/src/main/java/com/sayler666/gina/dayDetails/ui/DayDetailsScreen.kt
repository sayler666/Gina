package com.sayler666.gina.dayDetails.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.file.Files
import com.sayler666.core.flow.Event
import com.sayler666.core.flow.withValue
import com.sayler666.gina.attachments.ui.FilePreview
import com.sayler666.gina.attachments.ui.ImagePreview
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity.NonImage
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel
import com.sayler666.gina.destinations.DayDetailsEditScreenDestination
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.FullImageDialogDestination
import com.sayler666.gina.friends.ui.FriendIcon
import com.sayler666.gina.friends.viewmodel.FriendEntity
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.richeditor.setTextOrHtml
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import mood.ui.mapToMoodIcon


data class DayDetailsScreenNavArgs(
    val dayId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination(navArgsDelegate = DayDetailsScreenNavArgs::class)
@Composable
fun DayDetailsScreen(
    destinationsNavigator: DestinationsNavigator,
    navController: NavController,
    viewModel: DayDetailsViewModel = hiltViewModel(),
    vm: GinaMainViewModel = hiltViewModel(),
) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    NavigationBarColor(theme = theme)
    val requester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val day: DayDetailsEntity? by viewModel.day.collectAsStateWithLifecycle(null)
    val goToDay: Event<Int> by viewModel.goToDayId.collectAsStateWithLifecycle()
    goToDay.withValue { dayId ->
        destinationsNavigator.navigate(DayDetailsScreenDestination(DayDetailsScreenNavArgs(dayId))) {
            popUpTo(DayDetailsScreenDestination.route) { inclusive = true }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.error.collectLatest {
            it?.let {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    day?.let { day ->
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            DayTitle(day.dayOfMonth, day.dayOfWeek, day.yearAndMonth)
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
                        day.mood?.mapToMoodIcon()?.let { icon ->
                            Icon(
                                rememberVectorPainter(image = icon.icon),
                                tint = icon.color,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        IconButton(onClick = {
                            day.id?.let {
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
                    AttachmentsRow(day, destinationsNavigator)
                    Text(day)
                }
            },
            bottomBar = {
                if (day.friendsSelected.isNotEmpty())
                    BottomAppBar(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .wrapContentHeight(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        content = { FriendsRow(day.friendsSelected) }
                    )
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
    }

    LaunchedEffect(Unit) {
        delay(300)
        requester.requestFocus()
    }
}

@Composable
private fun Text(it: DayDetailsEntity) {
    val richTextState = rememberRichTextState()
    richTextState.setTextOrHtml(it.content)

    RichText(
        state = richTextState,
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalLayoutApi::class)
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
                                FullImageDialogDestination(
                                    attachment.bytes,
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
                                attachment.bytes,
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
    val context = LocalContext.current

    LazyRow(
        contentPadding = PaddingValues(start = 16.dp),
        content = {
            items(friends) { friend ->
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
        })
}
