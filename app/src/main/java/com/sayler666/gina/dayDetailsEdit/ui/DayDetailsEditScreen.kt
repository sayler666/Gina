package com.sayler666.gina.dayDetailsEdit.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.calendar.ui.DatePickerDialog
import com.sayler666.gina.core.compose.conditional
import com.sayler666.gina.core.file.Files
import com.sayler666.gina.core.file.Files.openFileIntent
import com.sayler666.gina.core.file.handleSelectedFiles
import com.sayler666.gina.core.flow.Event
import com.sayler666.gina.dayDetails.ui.FilePreview
import com.sayler666.gina.dayDetails.ui.ImagePreview
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.NonImage
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetails.viewmodel.FriendEntity
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.FullImageDestination
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.FriendIcon
import com.sayler666.gina.ui.FriendsPicker
import com.sayler666.gina.ui.Mood
import com.sayler666.gina.ui.MoodIcon
import com.sayler666.gina.ui.MoodPicker
import com.sayler666.gina.ui.VerticalDivider
import com.sayler666.gina.ui.mapToMoodIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class DayDetailsEditScreenNavArgs(
    val dayId: Int
)

val WindowInsets.Companion.isImeVisible: Boolean
    @Composable
    get() {
        val density = LocalDensity.current
        val ime = this.ime
        return remember {
            derivedStateOf {
                ime.getBottom(density) > 0
            }
        }.value
    }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
        route = DayDetailsScreenDestination,
        inclusive = true
    )

    // dialogs
    val showDeleteConfirmationDialog = remember { mutableStateOf(false) }
    DeleteConfirmationDialog(showDeleteConfirmationDialog) { viewModel.removeDay() }
    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }
    DiscardConfirmationDialog(showDiscardConfirmationDialog) { navController.popBackStack() }
    val showDatePickerPopup = remember { mutableStateOf(false) }

    BackHandler(onBack = {
        handleBackPress(changesExist, showDiscardConfirmationDialog, navController)
    })
    val isKeyboardOpen by keyboardAsState() // true or false
    currentDay?.let { day ->
        Scaffold(
            modifier = Modifier.imePadding(),
            topBar = {
                TopBar(
                    day,
                    onNavigateBackClicked = {
                        handleBackPress(
                            changesExist,
                            showDiscardConfirmationDialog,
                            navController
                        )
                    },
                    onChangeDateClicked = {
                        showDatePickerPopup.value = true
                    }
                )
            },
            bottomBar = {
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
                    }
                )
            },
            content = { scaffoldPadding ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                        .consumeWindowInsets(scaffoldPadding)
                        .systemBarsPadding()
                ) {
                    DatePickerDialog(
                        showDatePickerPopup.value,
                        initialDate = day.localDate,
                        onDismiss = {
                            showDatePickerPopup.value = false
                        },
                        onDateChanged = { date ->
                            viewModel.setNewDate(date)
                        }
                    )
                    Column {
                        AnimatedVisibility(
                            visible = !isKeyboardOpen
                        ) {
                            Attachments(day, destinationsNavigator) { attachmentHash ->
                                viewModel.removeAttachment(attachmentHash)
                            }
                        }

                        ContentTextField(day) { content ->
                            viewModel.setNewContent(content)
                        }
                    }
                }
            })

    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Composable
fun ContentTextField(
    day: DayDetailsEntity,
    autoFocus: Boolean = false,
    onContentChanged: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    Row(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .conditional(autoFocus) {
                    focusRequester(focusRequester)
                },
            value = day.content,
            onValueChange = { onContentChanged(it) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )
    }
    if (autoFocus)
        LaunchedEffect(Unit, block = {
            focusRequester.requestFocus()
        })
}

@Composable
fun Attachments(
    day: DayDetailsEntity,
    destinationsNavigator: DestinationsNavigator,
    onRemoveAttachment: (Int) -> Unit
) {
    val context = LocalContext.current
    if (day.attachments.isNotEmpty())
        FlowRow(modifier = Modifier.padding(16.dp, 0.dp)) {
            day.attachments.forEach { attachment ->
                when (attachment) {
                    is Image -> ImagePreview(
                        attachment,
                        onClick = {
                            destinationsNavigator.navigate(
                                FullImageDestination(attachment.byte, attachment.mimeType)
                            )
                        },
                        onRemoveClicked = {
                            onRemoveAttachment(attachment.byte.hashCode())
                        })
                    is NonImage -> FilePreview(
                        attachment,
                        onClick = {
                            openFileIntent(
                                context,
                                attachment.byte,
                                attachment.mimeType
                            )
                        },
                        onRemoveClicked = {
                            onRemoveAttachment(attachment.byte.hashCode())
                        })
                }
            }
        }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    day: DayDetailsEntity,
    onNavigateBackClicked: () -> Unit,
    onChangeDateClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onChangeDateClicked()
                }) {
                DayTitle(day.dayOfMonth, day.dayOfWeek, day.yearAndMonth)
                Icon(
                    Filled.ArrowDropDown,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = null
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { onNavigateBackClicked() }
            ) {
                Icon(Filled.ArrowBack, null)
            }
        })
}

@Composable
fun SaveFab(onSaveButtonClicked: () -> Unit) {
    FloatingActionButton(
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.primary,
        onClick = { onSaveButtonClicked() }
    ) {
        Icon(
            Filled.Save,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surfaceVariant
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
    onFriendClicked: (Int, Boolean) -> Unit
) {
    val showMoodPopup = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    BottomAppBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
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
        },
        floatingActionButton = { SaveFab { onSaveChanges() } }
    )
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
        true -> Box(modifier = Modifier.clickable(
            indication = rememberRipple(bounded = false),
            interactionSource = remember {
                MutableInteractionSource()
            }
        ) { showFriendsPopup.value = true }) {
            friends.take(2)
                .forEachIndexed { i, friend ->
                    FriendIcon(
                        friend = friend,
                        size = 32.dp,
                        modifier = Modifier
                            .offset(i * 5.dp)
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
    val moodIcon: MoodIcon = currentDay.mood.mapToMoodIcon()
    IconButton(onClick = { showMoodPopup.value = true }) {
        Icon(
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
        }
    )
}

@Composable
fun DeleteConfirmationDialog(showDialog: MutableState<Boolean>, onConfirmAction: () -> Unit) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = { Text(text = "Remove day") },
            text = { Text("Do you really want to remove this day?") },
            confirmButton = {
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                        showDialog.value = false
                        onConfirmAction()
                    }
                ) { Text("Remove") }
            },
            dismissButton = {
                OutlinedButton(
                    shape = MaterialTheme.shapes.medium,
                    onClick = { showDialog.value = false }
                ) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun DiscardConfirmationDialog(showDialog: MutableState<Boolean>, onDiscardAction: () -> Unit) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = { Text(text = "Discard changes") },
            text = { Text("Do you really want to discard changes?") },
            confirmButton = {
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                        showDialog.value = false
                        onDiscardAction()
                    }
                ) { Text("Discard") }
            },
            dismissButton = {
                OutlinedButton(
                    shape = MaterialTheme.shapes.medium,
                    onClick = { showDialog.value = false }
                ) { Text("Cancel") }
            }
        )
    }
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
