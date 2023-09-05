package com.sayler666.gina.addDay.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.sayler666.core.file.handleSelectedFiles
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel
import com.sayler666.gina.calendar.ui.DatePickerDialog
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetailsEdit.ui.Attachments
import com.sayler666.gina.dayDetailsEdit.ui.AttachmentsAmountLabel
import com.sayler666.gina.dayDetailsEdit.ui.Friends
import com.sayler666.gina.dayDetailsEdit.ui.Mood
import com.sayler666.gina.dayDetailsEdit.ui.SaveFab
import com.sayler666.gina.dayDetailsEdit.ui.TextFormat
import com.sayler666.gina.dayDetailsEdit.ui.handleBackPress
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.quotes.db.Quote
import com.sayler666.gina.ui.DayTitle
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
    val addAttachmentLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        handleSelectedFiles(it, context) { attachments -> viewModel.addAttachments(attachments) }
    }

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
                    addAttachmentLauncher,
                    onSaveChanges = { viewModel.saveChanges() },
                    onMoodChanged = { mood ->
                        viewModel.setNewMood(mood)
                        coroutineScope.launch {
                            delay(250)
                            autofocusOnContentText.value = true
                        }
                    },
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
                    onDateChanged = { date ->
                        viewModel.setNewDate(date)
                    }
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
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    day: DayDetailsEntity,
    onNavigateBackClicked: () -> Unit,
    onChangeDateClicked: () -> Unit,
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
private fun BottomBar(
    currentDay: DayDetailsEntity,
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
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            actions = {
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
            floatingActionButton = { SaveFab { onSaveChanges() } }
        )
    }

    LaunchedEffect(key1 = scope, block = {
        delay(300)
        showMoodPopup.value = true
    })
}
