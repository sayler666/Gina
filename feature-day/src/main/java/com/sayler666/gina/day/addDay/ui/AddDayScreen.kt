package com.sayler666.gina.day.addDay.ui

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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.sayler666.core.haptics.HapticFeedbackManager
import com.sayler666.domain.model.journal.Friend
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.attachments.ui.AttachmentState.AttachmentImageState
import com.sayler666.gina.attachments.ui.AttachmentState.AttachmentNonImageState
import com.sayler666.gina.calendar.ui.DatePickerDialog
import com.sayler666.gina.day.addDay.viewmodel.AddDayState
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.Back
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.DaySaved
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.NavToAttachment
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.ReinitializeText
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.ShowAttachmentPicker
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewAction.ShowDiscardDialog
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentPickerPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentRemove
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnAttachmentsAdded
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnContentChanged
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnFriendsChanged
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnMoodChanged
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnRestoreWorkingCopyPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnSaveChangesPressed
import com.sayler666.gina.day.addDay.viewmodel.AddDayViewModel.ViewEvent.OnSetNewDate
import com.sayler666.gina.day.attachments.ui.FileThumbnail
import com.sayler666.gina.day.attachments.ui.ImageThumbnail
import com.sayler666.gina.day.ui.AttachmentsButton
import com.sayler666.gina.day.ui.AttachmentsCountLabel
import com.sayler666.gina.day.ui.DayTopBar
import com.sayler666.gina.day.ui.FriendsPickerButton
import com.sayler666.gina.day.ui.MoodButton
import com.sayler666.gina.day.ui.SaveFab
import com.sayler666.gina.day.ui.TextFormatButton
import com.sayler666.gina.day.ui.rememberLauncherForMultipleImages
import com.sayler666.gina.feature.settings.ui.ImageOptimizationBottomSheet
import com.sayler666.gina.feature.settings.viewmodel.ImageOptimizationViewModel
import com.sayler666.gina.navigation.Navigator
import com.sayler666.gina.navigation.routes.ImagePreviewTmp
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.LocalHapticFeedbackManager
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.VerticalDivider
import com.sayler666.gina.ui.dialog.ConfirmationDialog
import com.sayler666.gina.ui.keyboardAsState
import com.sayler666.gina.ui.richeditor.RichTextEditor
import com.sayler666.gina.ui.richeditor.RichTextStyleRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID


@Composable
fun AddDayScreen(
    date: LocalDate?,
) {
    val key = rememberSaveable { UUID.randomUUID().toString() }
    val viewModel: AddDayViewModel =
        hiltViewModel<AddDayViewModel, AddDayViewModel.Factory>(key = key) {
            it.create(date)
        }
    val state: AddDayState? by viewModel.viewState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    val haptics = LocalHapticFeedbackManager.current

    var content by remember { mutableStateOf(TextFieldValue(state?.content ?: "")) }

    val showDiscardConfirmationDialog = rememberDiscardDialog()

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
            action = action,
            navigator = navigator,
            addAttachmentLauncher = addAttachmentLauncher,
            addAttachmentRequest = addAttachmentRequest,
            haptics = haptics,
            showDiscardConfirmationDialog = showDiscardConfirmationDialog,
            onReinitializeText = { content = TextFieldValue(it) }
        )
    }

    BackHandler(onBack = { viewModel.onViewEvent(OnBackPressed) })

    Content(
        state = state,
        textFieldValue = content,
        viewEvent = viewModel::onViewEvent,
        onFriendsPicked = { viewModel.onViewEvent(OnFriendsChanged(it)) },
    )
}

private fun onViewAction(
    action: ViewAction,
    navigator: Navigator,
    addAttachmentLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    addAttachmentRequest: PickVisualMediaRequest,
    showDiscardConfirmationDialog: MutableState<Boolean>,
    onReinitializeText: (String) -> Unit,
    haptics: HapticFeedbackManager,
) {
    when (action) {
        Back -> navigator.back()
        is NavToAttachment -> navigator.navigate(
            ImagePreviewTmp(
                image = action.image,
                mimeType = action.mimeType,
                hidden = action.hidden,
            )
        )
        ShowAttachmentPicker -> addAttachmentLauncher.launch(addAttachmentRequest)
        ShowDiscardDialog -> showDiscardConfirmationDialog.value = true
        is ReinitializeText -> onReinitializeText(action.content)
        DaySaved -> {
            haptics.newDayAdded()
            navigator.back()
        }
    }
}

@Composable
private fun Content(
    state: AddDayState?,
    textFieldValue: TextFieldValue,
    viewEvent: (ViewEvent) -> Unit,
    onFriendsPicked: (List<Friend>) -> Unit,
    imageOptimizationViewModel: ImageOptimizationViewModel = hiltViewModel(),
) {
    val imageOptimizationViewState by imageOptimizationViewModel.viewState.collectAsStateWithLifecycle()
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
                DayTopBar(
                    dayOfWeek = state.dayOfWeek,
                    dayOfMonth = state.dayOfMonth,
                    yearAndMonth = state.yearAndMonth,
                    onNavigateBackClicked = { viewEvent(OnBackPressed) },
                    onChangeDateClicked = { showDatePickerPopup.value = true },
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
                    showImageCompressSettingsDialog = showImageCompressSettingsDialog,
                    onFriendsPicked = onFriendsPicked,
                )
            }
        },
        content = { padding ->
            state?.let {
                DatePickerDialog(
                    showDatePickerPopup.value,
                    initialDate = it.localDate,
                    onDismiss = { showDatePickerPopup.value = false },
                    onDateChanged = { viewEvent(OnSetNewDate(it)) }
                )
            }
            Column(modifier = Modifier.padding(padding)) {
                state?.let { day ->
                    AnimatedVisibility(visible = !isKeyboardOpen) {
                        Attachments(attachments = day.attachments, viewEvent = viewEvent)
                    }
                    AnimatedVisibility(visible = isKeyboardOpen && day.attachments.isNotEmpty()) {
                        AttachmentsCountLabel(day.attachments.size)
                    }
                    RichTextEditor(
                        textFieldValue = textFieldValue,
                        richTextState = richTextState,
                        autoFocus = autofocusOnContentText.value,
                        quote = state.quote,
                        onContentChanged = { viewEvent(OnContentChanged(it)) }
                    )
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

@Composable
private fun rememberDiscardDialog(): MutableState<Boolean> {
    val navigator = LocalNavigator.current
    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }
    ConfirmationDialog(
        title = stringResource(R.string.day_discard_changes_title),
        text = stringResource(R.string.day_discard_changes_text),
        confirmButtonText = stringResource(R.string.day_discard_confirm),
        dismissButtonText = stringResource(R.string.day_cancel),
        showDialog = showDiscardConfirmationDialog
    ) { navigator.back() }
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
    onFriendsPicked: (List<Friend>) -> Unit,
) {
    val showMoodPopup = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showMoodPopup.value = true
    }
    val coroutineScope = rememberCoroutineScope()

    Column {
        RichTextStyleRow(modifier = Modifier.fillMaxWidth(), state = richTextState, showFormatRow = showFormatRow)
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            actions = {
                AttachmentsButton(
                    onClick = { viewEvent(OnAttachmentPickerPressed) },
                    onLongClick = { showImageCompressSettingsDialog.value = true }
                )
                FriendsPickerButton(
                    initialFriends = emptyList(),
                    onFriendsPicked = onFriendsPicked,
                )
                MoodButton(
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
                TextFormatButton(showFormatRow)
            },
            floatingActionButton = { SaveFab { viewEvent(OnSaveChangesPressed) } }
        )
    }
}

@Composable
fun Attachments(
    attachments: List<AttachmentState>,
    viewEvent: (ViewEvent) -> Unit,
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
                    is AttachmentImageState -> ImageThumbnail(
                        state = attachment,
                        onClick = {
                            viewEvent(
                                OnAttachmentPressed(
                                    image = attachment.content,
                                    mimeType = attachment.mimeType,
                                    hidden = attachment.hidden,
                                )
                            )
                        },
                        onRemoveClicked = {
                            viewEvent(OnAttachmentRemove(attachment.content.hashCode()))
                        }
                    )
                    is AttachmentNonImageState -> FileThumbnail(
                        state = attachment,
                        onClick = {
                            openFileIntent(
                                context = context,
                                bytes = attachment.content,
                                mimeType = attachment.mimeType
                            )
                        },
                        onRemoveClicked = {
                            viewEvent(OnAttachmentRemove(attachment.content.hashCode()))
                        }
                    )
                }
            }
        }
    }
}
