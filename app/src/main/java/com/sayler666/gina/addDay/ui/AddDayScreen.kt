package com.sayler666.gina.addDay.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.addDay.viewmodel.AddDayViewModel
import com.sayler666.gina.core.file.Files
import com.sayler666.gina.core.file.handleSelectedFiles
import com.sayler666.gina.core.flow.Event
import com.sayler666.gina.dayDetails.viewmodel.DayWithAttachmentsEntity
import com.sayler666.gina.dayDetailsEdit.ui.Attachments
import com.sayler666.gina.dayDetailsEdit.ui.ContentTextField
import com.sayler666.gina.dayDetailsEdit.ui.DiscardConfirmationDialog
import com.sayler666.gina.dayDetailsEdit.ui.SaveFab
import com.sayler666.gina.dayDetailsEdit.ui.handleBackPress
import com.sayler666.gina.daysList.viewmodel.Mood
import com.sayler666.gina.ui.DatePicker
import com.sayler666.gina.ui.MoodIcon
import com.sayler666.gina.ui.MoodPicker
import com.sayler666.gina.ui.mapToMoodIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddDayScreenNavArgs(
    val date: LocalDate? = null
)

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterial3Api::class)
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
        handleSelectedFiles(it, context) { content, mimeType ->
            viewModel.addAttachment(content, mimeType)
        }
    }

    val dayTemp: DayWithAttachmentsEntity? by viewModel.tempDay.collectAsStateWithLifecycle()

    val navigateBack: Event<Unit> by viewModel.navigateBack.collectAsStateWithLifecycle()
    if (navigateBack is Event.Value<Unit>) destinationsNavigator.popBackStack()

    // scroll
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.maxValue) { scrollState.scrollTo(scrollState.maxValue) }

    // dialogs
    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }
    DiscardConfirmationDialog(showDiscardConfirmationDialog) { navController.popBackStack() }

    BackHandler(onBack = {
        handleBackPress(true, showDiscardConfirmationDialog, navController)
    })

    Scaffold(
        topBar = {
            dayTemp?.let {
                TopBar(
                    it,
                    onNavigateBackClicked = {
                        handleBackPress(true, showDiscardConfirmationDialog, navController)
                    },
                    onDateButtonClicked = { date -> viewModel.setNewDate(date) }
                )
            }
        },
        bottomBar = {
            BottomBar(
                dayTemp,
                addAttachmentLauncher,
                onSaveChanges = { viewModel.saveChanges() },
                onMoodChanged = { mood -> viewModel.setNewMood(mood) }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(scrollState)
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
    day: DayWithAttachmentsEntity,
    onDateButtonClicked: (Long) -> Unit,
    onNavigateBackClicked: () -> Unit
) {
    TopAppBar(
        title = {
            DatePicker(day.dateTimestamp) { onDateButtonClicked(it) }
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
    currentDay: DayWithAttachmentsEntity?,
    addAttachmentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onSaveChanges: () -> Unit,
    onMoodChanged: (Mood) -> Unit
) {
    val showPopup = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        actions = {
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


