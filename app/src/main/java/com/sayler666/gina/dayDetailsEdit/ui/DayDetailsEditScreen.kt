package com.sayler666.gina.dayDetailsEdit.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.file.Files
import com.sayler666.core.file.Files.openFileIntent
import com.sayler666.core.file.handleSelectedFiles
import com.sayler666.core.flow.Event
import com.sayler666.gina.attachments.ui.FilePreview
import com.sayler666.gina.attachments.ui.ImagePreview
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity.NonImage
import com.sayler666.gina.calendar.ui.DatePickerDialog
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.FullImageDialogDestination
import com.sayler666.gina.friends.ui.FriendIcon
import com.sayler666.gina.friends.ui.FriendsPicker
import com.sayler666.gina.friends.viewmodel.FriendEntity
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.VerticalDivider
import com.sayler666.gina.ui.dialog.ConfirmationDialog
import com.sayler666.gina.ui.keyboardAsState
import com.sayler666.gina.ui.richeditor.RichTextEditor
import com.sayler666.gina.ui.richeditor.RichTextStyleRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mood.Mood
import mood.ui.MoodIcon
import mood.ui.MoodPicker
import mood.ui.mapToMoodIcon


data class DayDetailsEditScreenNavArgs(
    val dayId: Int
)

@RootNavGraph
@Destination(
    navArgsDelegate = DayDetailsEditScreenNavArgs::class
)
@Composable
fun DayDetailsEditScreen(
    destinationsNavigator: DestinationsNavigator,
    navController: NavController,
    viewModel: DayDetailsEditViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val addAttachmentLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        handleSelectedFiles(it, context) { attachments ->
            viewModel.addAttachments(attachments)
        }
    }

    val dayStored: DayDetailsEntity? by viewModel.day.collectAsStateWithLifecycle()
    val dayTemp: DayDetailsEntity? by viewModel.tempDay.collectAsStateWithLifecycle()
    val currentDay = dayTemp ?: dayStored
    val changesExist: Boolean by viewModel.changesExist.collectAsStateWithLifecycle()

    val navigateBack: Event<Unit> by viewModel.navigateBack.collectAsStateWithLifecycle()
    if (navigateBack is Event.Value<Unit>) destinationsNavigator.popBackStack()

    val navigateToList: Event<Unit> by viewModel.navigateToList.collectAsStateWithLifecycle()
    if (navigateToList is Event.Value<Unit>) destinationsNavigator.popBackStack(
        route = DayDetailsScreenDestination, inclusive = true
    )

    // dialogs
    val showDeleteConfirmationDialog = remember { mutableStateOf(false) }
    ConfirmationDialog(
        title = "Remove day",
        text = "Do you really want to remove this day?",
        confirmButtonText = "Remove",
        dismissButtonText = "Cancel",
        showDialog = showDeleteConfirmationDialog,
    ) { viewModel.removeDay() }

    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }
    ConfirmationDialog(
        title = "Discard changes",
        text = "Do you really want to discard changes?",
        confirmButtonText = "Discard",
        dismissButtonText = "Cancel",
        showDialog = showDiscardConfirmationDialog,
    ) { navController.popBackStack() }

    val showDatePickerPopup = remember { mutableStateOf(false) }

    BackHandler(onBack = {
        handleBackPress(changesExist, showDiscardConfirmationDialog, navController)
    })

    val isKeyboardOpen by keyboardAsState()

    val richTextState = rememberRichTextState()

    val showFormatRow = remember { mutableStateOf(false) }

    currentDay?.let { day ->
        Scaffold(topBar = {
            TopBar(day, onNavigateBackClicked = {
                handleBackPress(
                    changesExist, showDiscardConfirmationDialog, navController
                )
            }, onChangeDateClicked = {
                showDatePickerPopup.value = true
            })
        }, bottomBar = {
            BottomBar(
                day,
                showDeleteConfirmationDialog,
                addAttachmentLauncher,
                onSaveChanges = { viewModel.saveChanges() },
                onMoodChanged = { mood -> viewModel.setNewMood(mood) },
                onSearchChanged = { search ->
                    viewModel.searchFriend(search)
                },
                onAddNewFriend = { newFriend ->
                    viewModel.addNewFriend(newFriend)
                },
                onFriendClicked = { id, selected ->
                    viewModel.friendSelect(id, selected)
                },
                richTextState = richTextState,
                showFormatRow = showFormatRow
            )
        }, content = { scaffoldPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
            ) {
                DatePickerDialog(showDatePickerPopup.value,
                    initialDate = day.localDate,
                    onDismiss = {
                        showDatePickerPopup.value = false
                    },
                    onDateChanged = { date ->
                        viewModel.setNewDate(date)
                    })
                Column {
                    AnimatedVisibility(
                        visible = !isKeyboardOpen
                    ) {
                        Attachments(day, destinationsNavigator) { attachmentHash ->
                            viewModel.removeAttachment(attachmentHash)
                        }
                    }
                    AnimatedVisibility(
                        visible = isKeyboardOpen && day.attachments.isNotEmpty()
                    ) {
                        AttachmentsAmountLabel(day.attachments)
                    }

                    RichTextEditor(
                        richTextState = richTextState,
                        text = day.content,
                        onContentChanged = { content ->
                            viewModel.setNewContent(content)
                        }
                    )
                }
            }
        })
    }
}

@Composable
fun AttachmentsAmountLabel(
    attachments: List<AttachmentEntity>
) {
    Text(
        text = "Attachments: ${attachments.size}",
        style = MaterialTheme.typography.labelSmall
            .copy(color = MaterialTheme.colorScheme.primary),
        modifier = Modifier.padding(start = 16.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Attachments(
    day: DayDetailsEntity,
    destinationsNavigator: DestinationsNavigator,
    onRemoveAttachment: (Int) -> Unit
) {
    val context = LocalContext.current
    if (day.attachments.isNotEmpty()) FlowRow(modifier = Modifier.padding(16.dp, 0.dp)) {
        day.attachments.forEach { attachment ->
            when (attachment) {
                is Image -> ImagePreview(attachment, onClick = {
                    destinationsNavigator.navigate(
                        FullImageDialogDestination(attachment.bytes, attachment.mimeType)
                    )
                }, onRemoveClicked = {
                    onRemoveAttachment(attachment.bytes.hashCode())
                })

                is NonImage -> FilePreview(attachment, onClick = {
                    openFileIntent(
                        context, attachment.bytes, attachment.mimeType
                    )
                }, onRemoveClicked = {
                    onRemoveAttachment(attachment.bytes.hashCode())
                })
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    day: DayDetailsEntity, onNavigateBackClicked: () -> Unit, onChangeDateClicked: () -> Unit
) {
    TopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
            onChangeDateClicked()
        }) {
            DayTitle(day.dayOfMonth, day.dayOfWeek, day.yearAndMonth)
            Icon(
                Filled.ArrowDropDown,
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = null
            )
        }
    }, navigationIcon = {
        IconButton(onClick = { onNavigateBackClicked() }) {
            Icon(Filled.ArrowBack, null)
        }
    })
}

@Composable
fun SaveFab(onSaveButtonClicked: () -> Unit) {
    FloatingActionButton(shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.primary,
        onClick = { onSaveButtonClicked() }) {
        Icon(
            Filled.Save, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun BottomBar(
    currentDay: DayDetailsEntity,
    showDeleteConfirmationDialog: MutableState<Boolean>,
    addAttachmentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onSaveChanges: () -> Unit,
    onMoodChanged: (Mood) -> Unit,
    onSearchChanged: (String) -> Unit,
    onAddNewFriend: (String) -> Unit,
    onFriendClicked: (Int, Boolean) -> Unit,
    richTextState: RichTextState,
    showFormatRow: MutableState<Boolean>
) {
    val showMoodPopup = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column {
        RichTextStyleRow(
            modifier = Modifier.fillMaxWidth(),
            state = richTextState,
            showFormatRow = showFormatRow
        )
        BottomAppBar(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            actions = {
                Delete(showDeleteConfirmationDialog)

                VerticalDivider()

                Attachments(addAttachmentLauncher)

                Friends(
                    currentDay.friendsSelected,
                    onSearchChanged,
                    onAddNewFriend,
                    onFriendClicked,
                    currentDay
                )

                Mood(currentDay, showMoodPopup, scope, onMoodChanged)

                VerticalDivider()

                TextFormat(showFormatRow)
            },
            floatingActionButton = { SaveFab { onSaveChanges() } })
    }
}

@Composable
fun TextFormat(showFormat: MutableState<Boolean>) {
    IconButton(onClick = { showFormat.value = !showFormat.value }) {
        Icon(Filled.TextFormat, null)
    }
}

@Composable
private fun Delete(showDeleteConfirmationDialog: MutableState<Boolean>) {
    IconButton(onClick = { showDeleteConfirmationDialog.value = true }) {
        Icon(Filled.Delete, null)
    }
}

@Composable
fun Attachments(addAttachmentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    IconButton(onClick = {
        addAttachmentLauncher.launch(Files.selectFileIntent())
    }) {
        Icon(Filled.AddAPhoto, null)
    }
}

@Composable
fun Friends(
    friends: List<FriendEntity>,
    onSearchChanged: (String) -> Unit,
    onAddNewFriend: (String) -> Unit,
    onFriendClicked: (Int, Boolean) -> Unit,
    currentDay: DayDetailsEntity
) {
    val showFriendsPopup = remember { mutableStateOf(false) }

    when (friends.isNotEmpty()) {
        true -> Box(modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .clickable(indication = rememberRipple(bounded = false),
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
        onFriendClicked = { id, selected ->
            onFriendClicked(id, selected)
        },
        friends = currentDay.friendsAll
    )
}

@Composable
fun Mood(
    currentDay: DayDetailsEntity,
    showMoodPopup: MutableState<Boolean>,
    scope: CoroutineScope,
    onMoodChanged: (Mood) -> Unit
) {
    var initialization by remember {
        mutableStateOf(true)
    }
    val moodIcon: MoodIcon = currentDay.mood.mapToMoodIcon()
    var size by remember {
        mutableStateOf(1f)
    }
    val sizeAnimation by animateFloatAsState(
        targetValue = size, animationSpec = tween(durationMillis = 200), label = "Mood icon scale"
    )

    LaunchedEffect(key1 = currentDay.mood, block = {
        if (!initialization) {
            delay(50)
            size = 1.7f
            delay(200)
            size = 1f
        }
        initialization = false
    })

    IconButton(onClick = { showMoodPopup.value = true }) {
        Icon(
            modifier = Modifier.scale(scale = sizeAnimation),
            painter = rememberVectorPainter(image = moodIcon.icon),
            tint = moodIcon.color,
            contentDescription = null,
        )
    }
    MoodPicker(showMoodPopup.value,
        onDismiss = { showMoodPopup.value = false },
        onSelectMood = { mood ->
            scope.launch {
                delay(120)
                showMoodPopup.value = false
            }
            onMoodChanged(mood)
        })
}

fun handleBackPress(
    changesExist: Boolean,
    showDiscardConfirmationDialog: MutableState<Boolean>,
    navController: NavController
) {
    if (changesExist) {
        showDiscardConfirmationDialog.value = true
    } else {
        navController.popBackStack()
    }
}
