package com.sayler666.gina.day.dayDetailsEdit.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.file.Files.openFileIntent
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.calendar.ui.DatePickerDialog
import com.sayler666.gina.day.attachments.ui.FileThumbnail
import com.sayler666.gina.day.attachments.ui.ImageThumbnail
import com.sayler666.gina.day.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.Back
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.NavToList
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.OpenImagePreview
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.ReinitializeText
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.ShowAttachmentPicker
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewAction.ShowDiscardDialog
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentOpen
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentPickerPressed
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentRemove
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnAttachmentsAdded
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnContentChanged
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnFriendPressed
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnFriendSearchQueryChanged
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnMoodChanged
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnRemoveDayPressed
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnRestoreWorkingCopyPressed
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnSaveChangesPressed
import com.sayler666.gina.day.dayDetailsEdit.viewmodel.DayDetailsEditViewModel.ViewEvent.OnSetNewDate
import com.sayler666.gina.feature.settings.ui.ImageOptimizationBottomSheet
import com.sayler666.gina.feature.settings.viewmodel.ImageOptimizationViewModel
import com.sayler666.gina.navigation.routes.DayDetails
import com.sayler666.gina.navigation.routes.DayDetailsEdit
import com.sayler666.gina.navigation.routes.ImagePreviewTmp
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.VerticalDivider
import com.sayler666.gina.ui.dialog.ConfirmationDialog
import com.sayler666.gina.ui.keyboardAsState
import com.sayler666.gina.ui.richeditor.RichTextEditor
import com.sayler666.gina.ui.richeditor.RichTextStyleRow

@Composable
fun DayDetailsEditScreen(
    dayId: Int,
) {
    val viewModel: DayDetailsEditViewModel =
        hiltViewModel<DayDetailsEditViewModel, DayDetailsEditViewModel.Factory>(key = dayId.toString()) {
            it.create(dayId)
        }
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    val addAttachmentLauncher =
        rememberLauncherForMultipleImages(context = context) { attachments ->
            viewModel.onViewEvent(OnAttachmentsAdded(attachments))
        }
    val addAttachmentRequest: PickVisualMediaRequest = Builder().setMediaType(ImageOnly).build()

    var content by remember { mutableStateOf(TextFieldValue()) }
    LaunchedEffect(viewState.currentDay?.content != null) {
        viewState.currentDay?.content?.let { content = TextFieldValue(it) }
    }

    val showDeleteConfirmationDialog = remember { mutableStateOf(false) }
    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }

    ConfirmationDialog(
        title = stringResource(R.string.day_discard_changes_title),
        text = stringResource(R.string.day_discard_changes_text),
        confirmButtonText = stringResource(R.string.day_discard_confirm),
        dismissButtonText = stringResource(R.string.day_cancel),
        showDialog = showDiscardConfirmationDialog,
    ) { navigator.back() }

    ConfirmationDialog(
        title = stringResource(R.string.day_remove_title),
        text = stringResource(R.string.day_remove_text),
        confirmButtonText = stringResource(R.string.day_remove_confirm),
        dismissButtonText = stringResource(R.string.day_cancel),
        showDialog = showDeleteConfirmationDialog,
    ) { viewModel.onViewEvent(OnRemoveDayPressed) }

    BackHandler { viewModel.onViewEvent(OnBackPressed) }

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            Back -> navigator.back()
            NavToList -> navigator.popUntil { it !is DayDetails && it !is DayDetailsEdit }
            ShowAttachmentPicker -> addAttachmentLauncher.launch(addAttachmentRequest)
            ShowDiscardDialog -> showDiscardConfirmationDialog.value = true
            is OpenImagePreview -> navigator.navigate(
                ImagePreviewTmp(
                    image = action.attachmentState.content,
                    mimeType = action.attachmentState.mimeType,
                    attachmentId = action.attachmentState.id,
                    hidden = action.attachmentState.hidden,
                )
            )

            is ReinitializeText -> content = TextFieldValue(action.content)
        }
    }

    Content(
        viewState = viewState,
        textFieldValue = content,
        viewEvent = viewModel::onViewEvent,
        showDeleteConfirmationDialog = showDeleteConfirmationDialog,
    )
}

@Composable
private fun Content(
    viewState: DayDetailsEditViewModel.ViewState,
    textFieldValue: TextFieldValue,
    viewEvent: (ViewEvent) -> Unit,
    showDeleteConfirmationDialog: MutableState<Boolean>,
    imageOptimizationViewModel: ImageOptimizationViewModel = hiltViewModel(),
) {
    val imageOptimizationViewState by imageOptimizationViewModel.viewState.collectAsStateWithLifecycle()
    val showDatePickerPopup = remember { mutableStateOf(false) }
    val showImageCompressSettingsDialog = remember { mutableStateOf(false) }
    val isKeyboardOpen by keyboardAsState()
    val richTextState = rememberRichTextState()
    val showFormatRow = remember { mutableStateOf(false) }

    Scaffold(
        Modifier.imePadding(),
        topBar = {
            viewState.currentDay?.let { day ->
                TopBar(
                    dayOfMonth = day.dayOfMonth,
                    dayOfWeek = day.dayOfWeek,
                    yearAndMonth = day.yearAndMonth,
                    hasWorkingCopy = viewState.hasWorkingCopy,
                    onNavigateBackClicked = { viewEvent(OnBackPressed) },
                    onChangeDateClicked = { showDatePickerPopup.value = true },
                    onRestoreWorkingCopyClicked = { viewEvent(OnRestoreWorkingCopyPressed) }
                )
            }
        },
        bottomBar = {
            viewState.currentDay?.let { day ->
                BottomBar(
                    day,
                    showDeleteConfirmationDialog,
                    addAttachmentAction = { viewEvent(OnAttachmentPickerPressed) },
                    addAttachmentLongClickAction = { showImageCompressSettingsDialog.value = true },
                    onSaveChanges = { viewEvent(OnSaveChangesPressed) },
                    onMoodChanged = { viewEvent(OnMoodChanged(it)) },
                    onSearchChanged = { viewEvent(OnFriendSearchQueryChanged(it)) },
                    onAddNewFriend = { viewEvent(ViewEvent.OnAddNewFriend(it)) },
                    onFriendClicked = { id, selected -> viewEvent(OnFriendPressed(id, selected)) },
                    richTextState = richTextState,
                    showFormatRow = showFormatRow
                )
            }
        },
        content = { scaffoldPadding ->
            viewState.currentDay?.let { day ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                ) {
                    DatePickerDialog(
                        showDatePickerPopup.value,
                        initialDate = day.localDate,
                        onDismiss = { showDatePickerPopup.value = false },
                        onDateChanged = { viewEvent(OnSetNewDate(it)) }
                    )
                    Column {
                        AnimatedVisibility(visible = !isKeyboardOpen) {
                            Attachments(
                                attachments = day.attachments,
                                onViewEvent = viewEvent
                            )
                        }
                        AnimatedVisibility(visible = isKeyboardOpen && day.attachments.isNotEmpty()) {
                            AttachmentsCountLabel(day.attachments.size)
                        }
                        RichTextEditor(
                            textFieldValue = textFieldValue,
                            richTextState = richTextState,
                            onContentChanged = { viewEvent(OnContentChanged(it)) }
                        )
                    }
                }
            }

            imageOptimizationViewState.optimizationSettings?.let { settings ->
                ImageOptimizationBottomSheet(
                    showDialog = showImageCompressSettingsDialog.value,
                    imageOptimizationSettings = settings,
                    onDismiss = { showImageCompressSettingsDialog.value = false },
                    viewEvent = imageOptimizationViewModel::onViewEvent,
                )
            }
        })
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Attachments(
    attachments: List<AttachmentState>,
    onViewEvent: (ViewEvent) -> Unit,
) {
    val context = LocalContext.current
    if (attachments.isNotEmpty()) {
        FlowRow(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            attachments.forEach { attachment ->
                when (attachment) {
                    is AttachmentState.AttachmentImageState -> ImageThumbnail(
                        state = attachment,
                        onClick = {
                            onViewEvent(OnAttachmentOpen(attachment))
                        },
                        onRemoveClicked = {
                            onViewEvent(OnAttachmentRemove(attachment.content.hashCode()))
                        }
                    )

                    is AttachmentState.AttachmentNonImageState -> FileThumbnail(
                        state = attachment,
                        onClick = {
                            attachment.content.let { image ->
                                openFileIntent(context, image, attachment.mimeType)
                            }
                        },
                        onRemoveClicked = {
                            onViewEvent(OnAttachmentRemove(attachment.content.hashCode()))
                        }
                    )
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
