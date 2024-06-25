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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.file.Files
import com.sayler666.core.string.getTextWithoutHtml
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.attachments.ui.FileThumbnail
import com.sayler666.gina.attachments.ui.ImagePreviewScreenNavArgs
import com.sayler666.gina.attachments.ui.ImageThumbnail
import com.sayler666.gina.dayDetails.ui.Way.NEXT
import com.sayler666.gina.dayDetails.ui.Way.NONE
import com.sayler666.gina.dayDetails.ui.Way.PREVIOUS
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsState
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.Back
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToAttachment
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToDayDetails
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToNextDay
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.NavToPreviousDay
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewAction.ShowSnackBar
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnAttachmentPressed
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnDayDetailsPressed
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnNextDayPressed
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnPreviousDayPressed
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel.ViewEvent.OnResume
import com.sayler666.gina.destinations.DayDetailsEditScreenDestination
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.ImagePreviewScreenDestination
import com.sayler666.gina.friends.ui.FriendIcon
import com.sayler666.gina.friends.ui.FriendState
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.richeditor.WordCharsCounter
import com.sayler666.gina.ui.richeditor.setTextOrHtml
import kotlinx.coroutines.delay

data class DayDetailsScreenNavArgs(
    val dayId: Int,
    val way: Way = NONE
)

enum class Way {
    NEXT, PREVIOUS, NONE
}

@Destination(
    navArgsDelegate = DayDetailsScreenNavArgs::class,
    style = DayDetailsTransitions::class
)
@Composable
fun DayDetailsScreen(
    destinationsNavigator: DestinationsNavigator,
) {
    val ginaMainViewModel: GinaMainViewModel = hiltViewModel()
    val theme by ginaMainViewModel.theme.collectAsStateWithLifecycle()
    NavigationBarColor(theme = theme)

    val viewModel: DayDetailsViewModel = hiltViewModel()
    val viewState: DayDetailsState? = viewModel.viewState.collectAsStateWithLifecycle().value

    val snackbarHostState = remember { SnackbarHostState() }

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        onViewAction(action, destinationsNavigator, snackbarHostState)
    }

    LifecycleStartEffect(Unit) {
        viewModel.onViewEvent(OnResume)
        onStopOrDispose {}
    }

    Content(
        state = viewState,
        viewEvent = viewModel::onViewEvent,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    state: DayDetailsState?,
    viewEvent: (ViewEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val requester = remember { FocusRequester() }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
        state?.let {
            TopAppBar(title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DayTitle(state.dayOfMonth, state.dayOfWeek, state.yearAndMonth)
                }
            }, navigationIcon = {
                IconButton(onClick = { viewEvent(OnBackPressed) }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            }, actions = {
                state.mood.mapToMoodIcon().let { icon ->
                    Icon(
                        rememberVectorPainter(image = icon.icon),
                        tint = icon.color,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                IconButton(onClick = {
                    viewEvent(OnDayDetailsPressed)
                }) {
                    Icon(Icons.Filled.Edit, null)
                }
            })
        }
    }, content = { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            state?.let {
                AttachmentsRow(state, viewEvent)
                Text(state)
            }
        }
    }, bottomBar = {
        state?.let {
            if (it.friends.isNotEmpty()) BottomAppBar(containerColor = MaterialTheme.colorScheme.surface,
                content = { FriendsRow(it.friends) })
        }
    }, modifier = Modifier
        .onKeyEvent {
            if (it.type != KeyEventType.KeyDown) return@onKeyEvent false
            when (it.key) {
                VolumeUp -> {
                    viewEvent(OnNextDayPressed)
                    return@onKeyEvent true
                }

                VolumeDown -> {
                    viewEvent(OnPreviousDayPressed)
                    return@onKeyEvent true
                }

                else -> return@onKeyEvent false
            }
        }
        .focusRequester(requester)
        .focusable())

    LaunchedEffect(Unit) {
        delay(300)
        requester.requestFocus()
    }
}

private suspend fun onViewAction(
    action: ViewAction,
    destinationsNavigator: DestinationsNavigator,
    snackbarHostState: SnackbarHostState,
) {
    when (action) {
        Back -> destinationsNavigator.popBackStack()
        is NavToNextDay -> goToDay(destinationsNavigator, action.dayId, NEXT)
        is NavToPreviousDay -> goToDay(destinationsNavigator, action.dayId, PREVIOUS)

        is ShowSnackBar -> {
            snackbarHostState.showSnackbar(
                message = action.message,
                duration = SnackbarDuration.Short
            )
        }

        is NavToDayDetails -> destinationsNavigator.navigate(
            DayDetailsEditScreenDestination(action.dayId)
        )

        is NavToAttachment -> destinationsNavigator.navigate(
            ImagePreviewScreenDestination(
                ImagePreviewScreenNavArgs(
                    attachmentId = action.attachmentId,
                    allowNavigationToDayDetails = false
                )
            )
        )
    }
}

private fun goToDay(
    destinationsNavigator: DestinationsNavigator,
    dayId: Int,
    way: Way
) {
    destinationsNavigator.navigate(
        DayDetailsScreenDestination(DayDetailsScreenNavArgs(dayId = dayId, way = way))
    ) {
        popUpTo(DayDetailsScreenDestination.route) { inclusive = true }
    }
}

@Composable
private fun Text(state: DayDetailsState) {
    val richTextState = rememberRichTextState()
    richTextState.setTextOrHtml(state.content)

    Column(modifier = Modifier.padding(16.dp, 8.dp)) {
        WordCharsCounter(text = state.content.getTextWithoutHtml())
        SelectionContainer {
            RichText(
                state = richTextState,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AttachmentsRow(
    state: DayDetailsState,
    viewEvent: (ViewEvent) -> Unit
) {
    if (state.attachments.isNotEmpty()) {
        val context = LocalContext.current
        FlowRow(modifier = Modifier.padding(16.dp, 0.dp)) {
            state.attachments.forEach { attachment ->
                when (attachment) {
                    is AttachmentState.AttachmentImageState -> ImageThumbnail(
                        attachment,
                        onClick = {
                            attachment.id?.let {
                                viewEvent(OnAttachmentPressed(it))
                            }
                        })

                    is AttachmentState.AttachmentNonImageState -> FileThumbnail(
                        attachment,
                        onClick = {
                            attachment.content?.let {
                                Files.openFileIntent(context, it, attachment.mimeType)
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun FriendsRow(friends: List<FriendState>) {
    val context = LocalContext.current

    LazyRow(contentPadding = PaddingValues(start = 16.dp), content = {
        items(friends) { friend ->
            FriendIcon(friend = friend,
                size = 42.dp,
                modifier = Modifier
                    .padding(end = 8.dp, top = 0.dp)
                    .clickable(indication = rememberRipple(bounded = false),
                        interactionSource = remember { MutableInteractionSource() }) {
                        Toast
                            .makeText(context, friend.name, Toast.LENGTH_SHORT)
                            .show()
                    })
        }
    })
}
