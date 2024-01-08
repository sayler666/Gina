package com.sayler666.gina.dayDetails.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sayler666.core.compose.ANIMATION_DURATION
import com.sayler666.core.compose.Bottom
import com.sayler666.core.compose.Top
import com.sayler666.core.compose.slideInVerticallyWithFade
import com.sayler666.core.file.Files
import com.sayler666.gina.appDestination
import com.sayler666.gina.attachments.ui.FileThumbnail
import com.sayler666.gina.attachments.ui.ImagePreviewScreenNavArgs
import com.sayler666.gina.attachments.ui.ImageThumbnail
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity.NonImage
import com.sayler666.gina.dayDetails.ui.Way.*
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsViewModel
import com.sayler666.gina.destinations.DayDetailsEditScreenDestination
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.ImagePreviewScreenDestination
import com.sayler666.gina.friends.ui.FriendIcon
import com.sayler666.gina.friends.viewmodel.FriendEntity
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.richeditor.setTextOrHtml
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


data class DayDetailsScreenNavArgs(
    val dayId: Int,
    val way: Way = NONE
)

enum class Way {
    NEXT, PREVIOUS, NONE
}

object DayDetailsTransitions : DestinationStyle.Animated {

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition()
            : EnterTransition? = when (initialState.appDestination()) {
        DayDetailsScreenDestination -> {
            val way = targetState.arguments?.getSerializable(
                DayDetailsScreenNavArgs::way.name,
                Way::class.java
            ) ?: NONE

            when (way) {
                NEXT -> slideInVerticallyWithFade(Bottom)
                PREVIOUS -> slideInVerticallyWithFade(Top)
                NONE -> fadeIn(animationSpec = tween(ANIMATION_DURATION))
            }
        }

        else -> null
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition()
            : ExitTransition? = when (targetState.appDestination()) {
        DayDetailsScreenDestination -> fadeOut(animationSpec = tween(ANIMATION_DURATION))

        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination(
    navArgsDelegate = DayDetailsScreenNavArgs::class,
    style = DayDetailsTransitions::class
)
@Composable
fun DayDetailsScreen(
    destinationsNavigator: DestinationsNavigator,
    navController: NavController,
    viewModel: DayDetailsViewModel = hiltViewModel(),
    vm: GinaMainViewModel = hiltViewModel()
) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    NavigationBarColor(theme = theme)
    val requester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val day: DayDetailsEntity? by viewModel.day.collectAsStateWithLifecycle(null)

    LaunchedEffect(Unit) {
        viewModel.goToDayId.collectLatest { (id, direction) ->
            id.let { dayId ->
                destinationsNavigator.navigate(
                    DayDetailsScreenDestination(
                        DayDetailsScreenNavArgs(
                            dayId, direction
                        )
                    )
                ) {
                    popUpTo(DayDetailsScreenDestination.route) { inclusive = true }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.error.collectLatest {
            it?.let { message ->
                snackbarHostState.showSnackbar(
                    message = message, duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
        TopAppBar(title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                day?.let {
                    DayTitle(it.dayOfMonth, it.dayOfWeek, it.yearAndMonth)
                }
            }
        }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        }, actions = {
            day?.mood?.mapToMoodIcon()?.let { icon ->
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
    }, content = { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            day?.let {
                AttachmentsRow(it, destinationsNavigator)
                Text(it)
            }
        }
    }, bottomBar = {
        day?.let {
            if (it.friendsSelected.isNotEmpty()) BottomAppBar(containerColor = MaterialTheme.colorScheme.surface,
                content = { FriendsRow(it.friendsSelected) })
        }
    }, modifier = Modifier
        .onKeyEvent {
            if (it.type != KeyEventType.KeyDown) return@onKeyEvent false
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
        .focusable())

    LaunchedEffect(Unit) {
        delay(300)
        requester.requestFocus()
    }
}

@Composable
private fun Text(it: DayDetailsEntity) {
    val richTextState = rememberRichTextState()
    richTextState.setTextOrHtml(it.content)
    SelectionContainer {
        RichText(
            state = richTextState, modifier = Modifier
                .padding(16.dp, 8.dp)
                .fillMaxWidth()
        )
    }
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
                    is Image -> ImageThumbnail(attachment, onClick = {
                        attachment.id?.let {
                            destinationsNavigator.navigate(
                                ImagePreviewScreenDestination(
                                    ImagePreviewScreenNavArgs(
                                        it, allowNavigationToDayDetails = false
                                    )
                                )
                            )
                        }
                    })

                    is NonImage -> FileThumbnail(attachment, onClick = {
                        Files.openFileIntent(
                            context, attachment.bytes, attachment.mimeType
                        )
                    })
                }
            }
        }
    }
}

@Composable
fun FriendsRow(friends: List<FriendEntity>) {
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
