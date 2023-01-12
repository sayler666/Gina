package com.sayler666.gina.dayDetailsEdit.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
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
import com.sayler666.gina.dayDetails.viewmodel.DayWithAttachmentsEntity
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.FullImageDestination
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.Mood
import com.sayler666.gina.ui.MoodIcon
import com.sayler666.gina.ui.MoodPicker
import com.sayler666.gina.ui.mapToMoodIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class DayDetailsEditScreenNavArgs(
    val dayId: Int
)

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterial3Api::class)
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
        handleSelectedFiles(it, context) { content, mimeType ->
            viewModel.addAttachment(content, mimeType)
        }
    }

    val day: DayWithAttachmentsEntity? by viewModel.day.collectAsStateWithLifecycle()
    val dayTemp: DayWithAttachmentsEntity? by viewModel.tempDay.collectAsStateWithLifecycle()
    val currentDay = if (dayTemp != null) dayTemp else day
    val changesExist: Boolean by viewModel.changesExist.collectAsStateWithLifecycle()

    val navigateBack: Event<Unit> by viewModel.navigateBack.collectAsStateWithLifecycle()
    if (navigateBack is Event.Value<Unit>) destinationsNavigator.popBackStack()

    val navigateToList: Event<Unit> by viewModel.navigateToList.collectAsStateWithLifecycle()
    if (navigateToList is Event.Value<Unit>) destinationsNavigator.popBackStack(
        route = DayDetailsScreenDestination,
        inclusive = true
    )

    // scroll
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.maxValue) { scrollState.scrollTo(scrollState.maxValue) }

    // dialogs
    val showDeleteConfirmationDialog = remember { mutableStateOf(false) }
    DeleteConfirmationDialog(showDeleteConfirmationDialog) { viewModel.removeDay() }
    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }
    DiscardConfirmationDialog(showDiscardConfirmationDialog) { navController.popBackStack() }
    val showDatePickerPopup = remember { mutableStateOf(false) }

    BackHandler(onBack = {
        handleBackPress(changesExist, showDiscardConfirmationDialog, navController)
    })

    Scaffold(
        topBar = {
            currentDay?.let {
                TopBar(
                    it,
                    onNavigateBackClicked = {
                        handleBackPress(changesExist, showDiscardConfirmationDialog, navController)
                    },
                    onChangeDateClicked = {
                        showDatePickerPopup.value = true
                    }
                )
            }
        },
        bottomBar = {
            BottomBar(
                currentDay,
                showDeleteConfirmationDialog,
                addAttachmentLauncher,
                onSaveChanges = { viewModel.saveChanges() },
                onMoodChanged = { mood -> viewModel.setNewMood(mood) }
            )
        },
        content = { padding ->
            currentDay?.let {
                DatePickerDialog(
                    showDatePickerPopup.value,
                    initialDate = it.localDate,
                    onDismiss = {
                        showDatePickerPopup.value = false
                    },
                    onDateChanged = { date ->
                        viewModel.setNewDate(date)
                    }
                )
            }
            Column(
                modifier = Modifier
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(scrollState)
            ) {
                currentDay?.let {
                    Attachments(it, destinationsNavigator) { attachmentHash ->
                        viewModel.removeAttachment(attachmentHash)
                    }
                    ContentTextField(it) { content ->
                        viewModel.setNewContent(content)
                    }
                }
            }
        })
}

@Composable
fun ContentTextField(
    day: DayWithAttachmentsEntity,
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
    day: DayWithAttachmentsEntity,
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
                                FullImageDestination(attachment.byte)
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
    day: DayWithAttachmentsEntity,
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
    currentDay: DayWithAttachmentsEntity?,
    showDeleteConfirmationDialog: MutableState<Boolean>,
    addAttachmentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onSaveChanges: () -> Unit,
    onMoodChanged: (Mood) -> Unit
) {
    val showPopup = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        actions = {
            IconButton(onClick = { showDeleteConfirmationDialog.value = true }) {
                Icon(Filled.Delete, null)
            }
            Spacer(Modifier.width(8.dp))
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(0.dp, 8.dp)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {
                addAttachmentLauncher.launch(Files.selectFileIntent())
            }) {
                Icon(Filled.AddAPhoto, null)
            }
            val moodIcon: MoodIcon = currentDay?.mood.mapToMoodIcon()
            IconButton(onClick = { showPopup.value = true }) {
                Icon(
                    painter = rememberVectorPainter(image = moodIcon.icon),
                    tint = moodIcon.tint,
                    contentDescription = null,
                )
            }
            MoodPicker(showPopup.value,
                onDismiss = { showPopup.value = false },
                onSelectMood = { mood ->
                    scope.launch {
                        delay(120)
                        showPopup.value = false
                    }
                    onMoodChanged(mood)
                }
            )

        },
        floatingActionButton = { SaveFab { onSaveChanges() } }
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
