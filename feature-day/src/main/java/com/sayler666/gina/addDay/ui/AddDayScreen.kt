package com.sayler666.gina.addDay.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.PickVisualMediaRequest.Builder
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.file.Files.openFileIntent
import com.sayler666.core.image.ImageOptimization.OptimizationSettings
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewAction
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewAction.Back
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewAction.NavToAttachment
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewAction.ShowAttachmentPicker
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewAction.ShowDiscardDialog
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAddNewFriend
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentPickerPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentRemove
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentsAdded
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnContentChanged
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnFriendPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnFriendSearchQueryChanged
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnImageCompressionToggled
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnImageQualityChanged
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnMoodChanged
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnRestoreWorkingCopyPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnSaveChangesPressed
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel.ViewEvent.OnSetNewDate
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.attachments.ui.AttachmentState.AttachmentImageState
import com.sayler666.gina.attachments.ui.AttachmentState.AttachmentNonImageState
import com.sayler666.gina.attachments.ui.FileThumbnail
import com.sayler666.gina.attachments.ui.ImageThumbnail
import com.sayler666.gina.calendar.ui.DatePickerDialog
import com.sayler666.gina.dayDetailsEdit.ui.AttachmentsButton
import com.sayler666.gina.dayDetailsEdit.ui.AttachmentsCountLabel
import com.sayler666.gina.dayDetailsEdit.ui.Friends
import com.sayler666.gina.dayDetailsEdit.ui.Mood
import com.sayler666.gina.dayDetailsEdit.ui.SaveFab
import com.sayler666.gina.dayDetailsEdit.ui.TextFormat
import com.sayler666.gina.dayDetailsEdit.ui.TopBar
import com.sayler666.gina.dayDetailsEdit.ui.rememberLauncherForMultipleImages
import com.sayler666.gina.feature.settings.ui.ImageCompressBottomSheet
import com.sayler666.gina.ui.VerticalDivider
import com.sayler666.gina.ui.dialog.ConfirmationDialog
import com.sayler666.gina.ui.keyboardAsState
import com.sayler666.gina.ui.richeditor.RichTextEditor
import com.sayler666.gina.ui.richeditor.RichTextStyleRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val ADD_DAY_URL = "gina://add_day"

@Composable
fun AddDayScreen(
    viewModel: AddDayViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToImagePreview: (ByteArray, String) -> Unit,
) {
    val state: AddDayState? by viewModel.viewState.collectAsStateWithLifecycle()
    val imageOptimizationSettings: OptimizationSettings? by viewModel.imageOptimizationSettings.collectAsStateWithLifecycle()

    var content by remember {
        mutableStateOf(TextFieldValue(state?.content ?: ""))
    }
    LaunchedEffect(Unit) {
        viewModel.reinitializeText.collectLatest {
            state?.content?.let {
                content = TextFieldValue(it)
            }
        }
    }

    val showDiscardConfirmationDialog = rememberDiscardDialog(onNavigateBack)

    val context = LocalContext.current
    val addAttachmentLauncher =
        rememberLauncherForMultipleImages(context = context) { attachments ->
            viewModel.onViewEvent(OnAttachmentsAdded(attachments))
        }
    val addAttachmentRequest: PickVisualMediaRequest = Builder()
        .setMediaType(ImageOnly)
        .build()

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        onViewAction(
            action,
            onNavigateBack,
            onNavigateToImagePreview,
            addAttachmentLauncher,
            addAttachmentRequest,
            showDiscardConfirmationDialog
        )
    }

    BackHandler(onBack = { viewModel.onViewEvent(OnBackPressed) })

    Content(
        state = state,
        textFieldValue = content,
        viewEvent = viewModel::onViewEvent,
        imageOptimizationSettings = imageOptimizationSettings,
    )
}

private suspend fun onViewAction(
    action: ViewAction,
    onNavigateBack: () -> Unit,
    onNavigateToImagePreview: (ByteArray, String) -> Unit,
    addAttachmentLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    addAttachmentRequest: PickVisualMediaRequest,
    showDiscardConfirmationDialog: MutableState<Boolean>,
) {
    when (action) {
        Back -> onNavigateBack()

        is NavToAttachment -> onNavigateToImagePreview(action.image, action.mimeType)

        ShowAttachmentPicker -> {
            addAttachmentLauncher.launch(addAttachmentRequest)
        }

        ShowDiscardDialog -> showDiscardConfirmationDialog.value = true
    }
}

@Composable
private fun Content(
    state: AddDayState?,
    textFieldValue: TextFieldValue,
    viewEvent: (ViewEvent) -> Unit,
    imageOptimizationSettings: OptimizationSettings?,
) {
    val showDatePickerPopup = remember { mutableStateOf(false) }

    val autofocusOnContentText = remember { mutableStateOf(false) }
    val isKeyboardOpen by keyboardAsState()
    val richTextState = rememberRichTextState()
    val showFormatRow = remember { mutableStateOf(false) }

    val showImageCompressSettingsDialog = remember { mutableStateOf(false) }

    Scaffold(
        Modifier.imePadding(),
        topBar = {
            state?.let {
                TopBar(
                    dayOfWeek = state.dayOfWeek,
                    dayOfMonth = state.dayOfMonth,
                    yearAndMonth = state.yearAndMonth,
                    onNavigateBackClicked = { viewEvent(OnBackPressed) },
                    onChangeDateClicked = {
                        showDatePickerPopup.value = true
                    },
                    hasWorkingCopy = it.workingCopyExists,
                    onRestoreWorkingCopyClicked = { viewEvent(OnRestoreWorkingCopyPressed) }
                )
            }
        },
        bottomBar = {
            state?.let {
                BottomBar(
                    it,
                    viewEvent = viewEvent,
                    richTextState = richTextState,
                    showFormatRow = showFormatRow,
                    autofocusOnContentText = autofocusOnContentText,
                    showImageCompressSettingsDialog = showImageCompressSettingsDialog
                )
            }
        },
        content = { padding ->
            state?.let {
                DatePickerDialog(
                    showDatePickerPopup.value,
                    initialDate = it.localDate,
                    onDismiss = {
                        showDatePickerPopup.value = false
                    },
                    onDateChanged = { viewEvent(OnSetNewDate(it)) }
                )
            }
            Column(
                modifier = Modifier
                    .padding(padding)
            ) {
                state?.let { day ->
                    AnimatedVisibility(
                        visible = !isKeyboardOpen
                    ) {
                        AttachmentsRow(
                            attachments = day.attachments,
                            viewEvent = viewEvent,
                        )
                    }
                    AnimatedVisibility(
                        visible = isKeyboardOpen && day.attachments.isNotEmpty()
                    ) {
                        AttachmentsCountLabel(day.attachments.size)
                    }

                    RichTextEditor(
                        textFieldValue = textFieldValue,
                        richTextState = richTextState,
                        autoFocus = autofocusOnContentText.value,
                        quote = state.quote,
                        onContentChanged = { content ->
                            viewEvent(OnContentChanged(content))
                        }
                    )
                }
            }

            ImageCompressBottomSheet(
                showDialog = showImageCompressSettingsDialog.value,
                imageOptimizationSettings = imageOptimizationSettings,
                onDismiss = { showImageCompressSettingsDialog.value = false },
                onSetImageQuality = { viewEvent(OnImageQualityChanged(it)) },
                onImageCompressionToggled = { viewEvent(OnImageCompressionToggled(it)) },
            )
        })
}

@Composable
private fun rememberDiscardDialog(onNavigateBack: () -> Unit): MutableState<Boolean> {
    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }
    ConfirmationDialog(
        title = "Discard changes",
        text = "Do you really want to discard changes?",
        confirmButtonText = "Discard",
        dismissButtonText = "Cancel",
        showDialog = showDiscardConfirmationDialog
    ) { onNavigateBack() }
    return showDiscardConfirmationDialog
}

@Composable
private fun BottomBar(
    state: AddDayState,
    viewEvent: (ViewEvent) -> Unit,
    richTextState: RichTextState,
    showFormatRow: MutableState<Boolean>,
    showImageCompressSettingsDialog: MutableState<Boolean>,
    autofocusOnContentText: MutableState<Boolean>,
) {
    val showMoodPopup = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showMoodPopup.value = true
    }
    val coroutineScope = rememberCoroutineScope()

    Column {
        RichTextStyleRow(
            modifier = Modifier.fillMaxWidth(),
            state = richTextState,
            showFormatRow = showFormatRow
        )
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            actions = {
                AttachmentsButton(
                    onClick = { viewEvent(OnAttachmentPickerPressed) },
                    onLongClick = { showImageCompressSettingsDialog.value = true }
                )

                Friends(
                    friends = state.friendsSelected,
                    allFriends = state.friendsAll,
                    onSearchChanged = { viewEvent(OnFriendSearchQueryChanged(it)) },
                    onAddNewFriend = { viewEvent(OnAddNewFriend(it)) },
                    onFriendClicked = { friendId, state ->
                        viewEvent(OnFriendPressed(friendId, state))
                    },
                )

                Mood(
                    mood = state.mood,
                    showMoodPopup = showMoodPopup,
                    onMoodChanged = {
                        viewEvent(OnMoodChanged(it))
                        coroutineScope.launch {
                            delay(250)
                            autofocusOnContentText.value = true
                        }
                    })

                VerticalDivider()

                TextFormat(showFormatRow)
            },
            floatingActionButton = { SaveFab { viewEvent(OnSaveChangesPressed) } }
        )
    }
}


@Composable
fun AttachmentsRow(
    attachments: List<AttachmentState>,
    viewEvent: (ViewEvent) -> Unit,
) {
    val context = LocalContext.current
    if (attachments.isNotEmpty()) FlowRow(modifier = Modifier.padding(16.dp, 0.dp)) {
        attachments.forEach { attachment ->
            when (attachment) {
                is AttachmentImageState -> ImageThumbnail(
                    state = attachment,
                    onClick = {
                        attachment.content.let { image ->
                            viewEvent(OnAttachmentPressed(image, attachment.mimeType))
                        }
                    },
                    onRemoveClicked = {
                        viewEvent(OnAttachmentRemove(attachment.content.hashCode()))
                    })

                is AttachmentNonImageState -> FileThumbnail(
                    state = attachment,
                    onClick = {
                        attachment.content.let { image ->
                            openFileIntent(
                                context, image, attachment.mimeType
                            )
                        }
                    },
                    onRemoveClicked = {
                        viewEvent(OnAttachmentRemove(attachment.content.hashCode()))
                    })
            }
        }
    }
}
