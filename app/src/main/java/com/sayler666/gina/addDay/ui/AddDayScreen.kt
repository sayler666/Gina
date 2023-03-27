package com.sayler666.gina.addDay.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel
import com.sayler666.gina.calendar.ui.DatePickerDialog
import com.sayler666.gina.core.file.handleSelectedFiles
import com.sayler666.gina.core.flow.Event
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetailsEdit.ui.Attachments
import com.sayler666.gina.dayDetailsEdit.ui.ContentTextField
import com.sayler666.gina.dayDetailsEdit.ui.DiscardConfirmationDialog
import com.sayler666.gina.dayDetailsEdit.ui.Friends
import com.sayler666.gina.dayDetailsEdit.ui.Mood
import com.sayler666.gina.dayDetailsEdit.ui.SaveFab
import com.sayler666.gina.dayDetailsEdit.ui.handleBackPress
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.Mood
import java.time.LocalDate

data class AddDayScreenNavArgs(
    val date: LocalDate? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination(
    navArgsDelegate = AddDayScreenNavArgs::class
)
@Composable
fun AddDayScreen(
    destinationsNavigator: DestinationsNavigator,
    navController: NavController,
    viewModel: AddDayViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val addAttachmentLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        handleSelectedFiles(it, context) { attachments ->
            viewModel.addAttachments(attachments)
        }
    }

    val dayTemp: DayDetailsEntity? by viewModel.tempDay.collectAsStateWithLifecycle()
    val changesExist: Boolean by viewModel.changesExist.collectAsStateWithLifecycle()

    val navigateBack: Event<Unit> by viewModel.navigateBack.collectAsStateWithLifecycle()
    if (navigateBack is Event.Value<Unit>) destinationsNavigator.popBackStack()

    // dialogs
    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }
    DiscardConfirmationDialog(showDiscardConfirmationDialog) { navController.popBackStack() }
    val showDatePickerPopup = remember { mutableStateOf(false) }

    BackHandler(onBack = {
        handleBackPress(changesExist, showDiscardConfirmationDialog, navController)
    })

    Scaffold(
        topBar = {
            dayTemp?.let {
                TopBar(
                    it,
                    onNavigateBackClicked = {
                        handleBackPress(true, showDiscardConfirmationDialog, navController)
                    },
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
                dayTemp?.let {
                    Attachments(it, destinationsNavigator) { attachmentHash ->
                        viewModel.removeAttachment(attachmentHash)
                    }
                    ContentTextField(it, autoFocus = true) { content ->
                        viewModel.setNewContent(content)
                    }
                }
            }
        })
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
    onFriendClicked: (Int, Boolean) -> Unit
) {
    val showPopup = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        actions = {
            Attachments(addAttachmentLauncher)

            Friends(
                currentDay.friendsSelected,
                onSearchChanged,
                onAddNewFriend,
                onFriendClicked,
                currentDay
            )

            Mood(currentDay, showPopup, scope, onMoodChanged)
        },
        floatingActionButton = { SaveFab { onSaveChanges() } }
    )
}


