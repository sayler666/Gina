package com.sayler666.gina.dayDetailsEdit.ui

import androidx.activity.compose.BackHandler
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.PickVisualMediaRequest.Builder
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.sayler666.core.file.Files.openFileIntent
import com.sayler666.core.image.ImageOptimization
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.attachments.ui.FileThumbnail
import com.sayler666.gina.attachments.ui.ImageThumbnail
import com.sayler666.gina.calendar.ui.DatePickerDialog
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel
import com.sayler666.gina.feature.settings.ui.ImageCompressBottomSheet
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.VerticalDivider
import com.sayler666.gina.ui.dialog.ConfirmationDialog
import com.sayler666.gina.ui.keyboardAsState
import com.sayler666.gina.ui.richeditor.RichTextEditor
import com.sayler666.gina.ui.richeditor.RichTextStyleRow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DayDetailsEditScreen(
    dayId: Int,
) {
    val viewModel: DayDetailsEditViewModel = hiltViewModel<DayDetailsEditViewModel, DayDetailsEditViewModel.Factory>(key = dayId.toString()) { it.create(dayId) }
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val addAttachmentLauncher =
        rememberLauncherForMultipleImages(context = context) { attachments ->
            viewModel.addAttachments(attachments)
        }
    val addAttachmentRequest: PickVisualMediaRequest = Builder()
        .setMediaType(ImageOnly)
        .build()

    val dayStored: DayDetailsEntity? by viewModel.day.collectAsStateWithLifecycle()
    val dayTemp: DayDetailsEntity? by viewModel.tempDay.collectAsStateWithLifecycle()
    val currentDay = dayTemp ?: dayStored
    val changesExist: Boolean by viewModel.changesExist.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToList.collectLatest {
            navigator.popUntil { it !is Route.DayDetails && it !is Route.DayDetailsEdit }
        }
    }

    val showDeleteConfirmationDialog = rememberConfirmationDialog(viewModel)
    val showDiscardConfirmationDialog = rememberDiscardDialog()
    val showDatePickerPopup = remember { mutableStateOf(false) }
    val workingCopy: Boolean by viewModel.hasWorkingCopy.collectAsStateWithLifecycle(false)

    fun onBackPress() {
        handleBackPress(changesExist, showDiscardConfirmationDialog, navigator::back)
    }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collectLatest { navigator.back() }
    }

    val imageOptimizationSettings: ImageOptimization.OptimizationSettings? by viewModel.imageOptimizationSettings.collectAsStateWithLifecycle()
    val showImageCompressSettingsDialog = remember { mutableStateOf(false) }

    val isKeyboardOpen by keyboardAsState()
    val richTextState = rememberRichTextState()
    val showFormatRow = remember { mutableStateOf(false) }
    var content by remember { mutableStateOf(TextFieldValue()) }
    LaunchedEffect(dayTemp?.content != null) {
        dayTemp?.content?.let { content = TextFieldValue(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.reinitializeText.collectLatest {
            dayTemp?.content?.let {
                content = TextFieldValue(it)
            }
        }
    }

    BackHandler(onBack = ::onBackPress)

    Scaffold(
        Modifier.imePadding(),
        topBar = {
            currentDay?.let { day ->
                TopBar(
                    dayOfMonth = day.dayOfMonth,
                    dayOfWeek = day.dayOfWeek,
                    yearAndMonth = day.yearAndMonth,
                    hasWorkingCopy = workingCopy,
                    onNavigateBackClicked = ::onBackPress,
                    onChangeDateClicked = {
                        showDatePickerPopup.value = true
                    },
                    onRestoreWorkingCopyClicked = {
                        viewModel.restoreWorkingCopy()
                    }
                )
            }
        },
        bottomBar = {
            currentDay?.let { day ->
                BottomBar(
                    day,
                    showDeleteConfirmationDialog,
                    addAttachmentAction = { addAttachmentLauncher.launch(addAttachmentRequest) },
                    addAttachmentLongClickAction = { showImageCompressSettingsDialog.value = true },
                    onSaveChanges = viewModel::saveChanges,
                    onMoodChanged = viewModel::setNewMood,
                    onSearchChanged = viewModel::searchFriend,
                    onAddNewFriend = viewModel::addNewFriend,
                    onFriendClicked = viewModel::friendSelect,
                    richTextState = richTextState,
                    showFormatRow = showFormatRow
                )
            }
        },
        content = { scaffoldPadding ->
            currentDay?.let { day ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                ) {
                    DatePickerDialog(
                        showDatePickerPopup.value,
                        initialDate = day.localDate,
                        onDismiss = {
                            showDatePickerPopup.value = false
                        },
                        onDateChanged = viewModel::setNewDate
                    )
                    Column {
                        AnimatedVisibility(
                            visible = !isKeyboardOpen
                        ) {
                            Attachments(day) { attachmentHash ->
                                viewModel.removeAttachment(attachmentHash)
                            }
                        }
                        AnimatedVisibility(
                            visible = isKeyboardOpen && day.attachments.isNotEmpty()
                        ) {
                            AttachmentsCountLabel(day.attachments.size)
                        }
                        RichTextEditor(
                            textFieldValue = content,
                            richTextState = richTextState,
                            onContentChanged = viewModel::setNewContent
                        )
                    }
                }
            }

            ImageCompressBottomSheet(
                showDialog = showImageCompressSettingsDialog.value,
                imageOptimizationSettings = imageOptimizationSettings,
                onDismiss = { showImageCompressSettingsDialog.value = false },
                onSetImageQuality = viewModel::setNewImageQuality,
                onImageCompressionToggled = viewModel::toggleImageCompression
            )
        })
}

@Composable
private fun rememberDiscardDialog(): MutableState<Boolean> {
    val navigator = LocalNavigator.current
    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }
    ConfirmationDialog(
        title = "Discard changes",
        text = "Do you really want to discard changes?",
        confirmButtonText = "Discard",
        dismissButtonText = "Cancel",
        showDialog = showDiscardConfirmationDialog,
    ) { navigator.back() }
    return showDiscardConfirmationDialog
}

@Composable
private fun rememberConfirmationDialog(viewModel: DayDetailsEditViewModel): MutableState<Boolean> {
    val showDeleteConfirmationDialog = remember { mutableStateOf(false) }
    ConfirmationDialog(
        title = "Remove day",
        text = "Do you really want to remove this day?",
        confirmButtonText = "Remove",
        dismissButtonText = "Cancel",
        showDialog = showDeleteConfirmationDialog,
    ) { viewModel.removeDay() }
    return showDeleteConfirmationDialog
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Attachments(
    day: DayDetailsEntity,
    onRemoveAttachment: (Int) -> Unit
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    if (day.attachments.isNotEmpty()) {
        FlowRow(
            modifier = Modifier
                .padding(16.dp, 0.dp)
                .padding(top = 16.dp)
        ) {
            day.attachments.forEach { attachment ->
                when (attachment) {
                    is AttachmentState.AttachmentImageState -> ImageThumbnail(
                        attachment,
                        onClick = {
                            attachment.content.let { image ->
                                navigator.navigate(Route.ImagePreviewTmp(image, attachment.mimeType))
                            }
                        },
                        onRemoveClicked = {
                            onRemoveAttachment(attachment.content.hashCode())
                        })

                    is AttachmentState.AttachmentNonImageState -> FileThumbnail(
                        attachment,
                        onClick = {
                            attachment.content.let { image ->
                                openFileIntent(
                                    context, image, attachment.mimeType
                                )
                            }
                        },
                        onRemoveClicked = {
                            onRemoveAttachment(attachment.content.hashCode())
                        })
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    currentDay: DayDetailsEntity,
    showDeleteConfirmationDialog: MutableState<Boolean>,
    addAttachmentAction: () -> Unit,
    addAttachmentLongClickAction: () -> Unit,
    onSaveChanges: () -> Unit,
    onMoodChanged: (Mood) -> Unit,
    onSearchChanged: (String) -> Unit,
    onAddNewFriend: (String) -> Unit,
    onFriendClicked: (Int, Boolean) -> Unit,
    richTextState: RichTextState,
    showFormatRow: MutableState<Boolean>
) {
    val showMoodPopup = remember { mutableStateOf(false) }
    Column {
        RichTextStyleRow(
            modifier = Modifier.fillMaxWidth(),
            state = richTextState,
            showFormatRow = showFormatRow
        )
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            actions = {
                Delete(showDeleteConfirmationDialog)

                VerticalDivider()

                AttachmentsButton(addAttachmentAction, onLongClick = addAttachmentLongClickAction)

                Friends(
                    friends = currentDay.friendsSelected,
                    onSearchChanged = onSearchChanged,
                    onAddNewFriend = onAddNewFriend,
                    onFriendClicked = onFriendClicked,
                    allFriends = currentDay.friendsAll,
                )

                Mood(currentDay.mood ?: Mood.EMPTY, showMoodPopup, onMoodChanged)

                VerticalDivider()

                TextFormat(showFormatRow)
            },
            floatingActionButton = { SaveFab { onSaveChanges() } })
    }
}

@Composable
private fun Delete(showDeleteConfirmationDialog: MutableState<Boolean>) {
    IconButton(onClick = { showDeleteConfirmationDialog.value = true }) {
        Icon(Filled.Delete, null)
    }
}

fun handleBackPress(
    changesExist: Boolean,
    showDiscardConfirmationDialog: MutableState<Boolean>,
    onNavigateBack: () -> Unit
) {
    if (changesExist) {
        showDiscardConfirmationDialog.value = true
    } else {
        onNavigateBack()
    }
}
