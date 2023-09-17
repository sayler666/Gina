package com.sayler666.gina.addDay.ui

import androidx.activity.compose.BackHandler
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.PickVisualMediaRequest.Builder
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.image.ImageOptimization
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel
import com.sayler666.gina.calendar.ui.DatePickerDialog
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetailsEdit.ui.Attachments
import com.sayler666.gina.dayDetailsEdit.ui.AttachmentsAmountLabel
import com.sayler666.gina.dayDetailsEdit.ui.Friends
import com.sayler666.gina.dayDetailsEdit.ui.Mood
import com.sayler666.gina.dayDetailsEdit.ui.SaveFab
import com.sayler666.gina.dayDetailsEdit.ui.TextFormat
import com.sayler666.gina.dayDetailsEdit.ui.TopBar
import com.sayler666.gina.dayDetailsEdit.ui.handleBackPress
import com.sayler666.gina.dayDetailsEdit.ui.rememberLauncherForMultipleImages
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.quotes.db.Quote
import com.sayler666.gina.settings.ui.ImageCompressBottomSheet
import com.sayler666.gina.ui.NavigationBarColor
import com.sayler666.gina.ui.VerticalDivider
import com.sayler666.gina.ui.dialog.ConfirmationDialog
import com.sayler666.gina.ui.keyboardAsState
import com.sayler666.gina.ui.richeditor.RichTextEditor
import com.sayler666.gina.ui.richeditor.RichTextStyleRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mood.Mood
import java.time.LocalDate

data class AddDayScreenNavArgs(
    val date: LocalDate? = null
)

@RootNavGraph
@Destination(
    navArgsDelegate = AddDayScreenNavArgs::class
)
@Composable
fun AddDayScreen(
    destinationsNavigator: DestinationsNavigator,
    navController: NavController,
    viewModel: AddDayViewModel = hiltViewModel(),
    vm: GinaMainViewModel = hiltViewModel(),
) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    NavigationBarColor(theme = theme)

    val context = LocalContext.current
    val addAttachmentLauncher =
        rememberLauncherForMultipleImages(context = context) { attachments ->
            viewModel.addAttachments(attachments)
        }
    val addAttachmentRequest: PickVisualMediaRequest = Builder()
        .setMediaType(ImageOnly)
        .build()

    val imageOptimizationSettings: ImageOptimization.OptimizationSettings? by viewModel.imageOptimizationSettings.collectAsStateWithLifecycle()
    val showImageCompressSettingsDialog = remember { mutableStateOf(false) }
    ImageCompressBottomSheet(
        showDialog = showImageCompressSettingsDialog.value,
        imageOptimizationSettings = imageOptimizationSettings,
        onDismiss = { showImageCompressSettingsDialog.value = false },
        onSetImageQuality = viewModel::setNewImageQuality,
        onImageCompressionToggled = viewModel::toggleImageCompression
    )

    val dayTemp: DayDetailsEntity? by viewModel.tempDay.collectAsStateWithLifecycle()
    val changesExist: Boolean by viewModel.changesExist.collectAsStateWithLifecycle()
    val quote: Quote? by viewModel.quote.collectAsStateWithLifecycle(null)

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collectLatest { destinationsNavigator.popBackStack() }
    }

    val showDiscardConfirmationDialog = rememberDiscardDialog(navController)
    val showDatePickerPopup = remember { mutableStateOf(false) }

    val autofocusOnContentText = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val isKeyboardOpen by keyboardAsState()
    val richTextState = rememberRichTextState()
    val showFormatRow = remember { mutableStateOf(false) }

    fun onBackPress() {
        handleBackPress(changesExist, showDiscardConfirmationDialog, navController)
    }
    BackHandler(onBack = ::onBackPress)
    Scaffold(
        Modifier.imePadding(),
        topBar = {
            dayTemp?.let {
                TopBar(
                    day = it,
                    onNavigateBackClicked = ::onBackPress,
                    onChangeDateClicked = {
                        showDatePickerPopup.value = true
                    }
                )
            }
        },
        bottomBar = {
            dayTemp?.let {
                BottomBar(
                    it,
                    addAttachmentAction = { addAttachmentLauncher.launch(addAttachmentRequest) },
                    addAttachmentLongClickAction = { showImageCompressSettingsDialog.value = true },
                    onSaveChanges = { viewModel.saveChanges() },
                    onMoodChanged = { mood ->
                        viewModel.setNewMood(mood)
                        coroutineScope.launch {
                            delay(250)
                            autofocusOnContentText.value = true
                        }
                    },
                    onSearchChanged = viewModel::searchFriend,
                    onAddNewFriend = viewModel::addNewFriend,
                    onFriendClicked = viewModel::friendSelect,
                    richTextState = richTextState,
                    showFormatRow = showFormatRow
                )
            }
        },
        content = { padding ->
            dayTemp?.let {
                DatePickerDialog(
                    showDatePickerPopup.value,
                    initialDate = it.localDate,
                    onDismiss = {
                        showDatePickerPopup.value = false
                    },
                    onDateChanged = viewModel::setNewDate
                )
            }
            Column(
                modifier = Modifier
                    .padding(padding)
            ) {
                dayTemp?.let { day ->

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
                        richTextState,
                        text = day.content,
                        autoFocus = autofocusOnContentText.value,
                        quote = quote,
                        onContentChanged = { content ->
                            viewModel.setNewContent(content)
                        }
                    )
                }
            }
        })
}

@Composable
private fun rememberDiscardDialog(navController: NavController): MutableState<Boolean> {
    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }
    ConfirmationDialog(
        title = "Discard changes",
        text = "Do you really want to discard changes?",
        confirmButtonText = "Discard",
        dismissButtonText = "Cancel",
        showDialog = showDiscardConfirmationDialog
    ) { navController.popBackStack() }
    return showDiscardConfirmationDialog
}

@Composable
private fun BottomBar(
    currentDay: DayDetailsEntity,
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

    LaunchedEffect(Unit) {
        delay(300)
        showMoodPopup.value = true
    }

    Column {
        RichTextStyleRow(
            modifier = Modifier.fillMaxWidth(),
            state = richTextState,
            showFormatRow = showFormatRow
        )
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            actions = {
                Attachments(onClick = addAttachmentAction, onLongClick = addAttachmentLongClickAction)

                Friends(
                    currentDay.friendsSelected,
                    onSearchChanged,
                    onAddNewFriend,
                    onFriendClicked,
                    currentDay
                )

                Mood(currentDay, showMoodPopup, onMoodChanged)

                VerticalDivider()

                TextFormat(showFormatRow)
            },
            floatingActionButton = { SaveFab { onSaveChanges() } }
        )
    }
}
