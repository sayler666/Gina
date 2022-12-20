package com.sayler666.gina.dayDetailsEdit.ui

import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.sayler666.gina.core.file.Files
import com.sayler666.gina.core.file.Files.openFileIntent
import com.sayler666.gina.core.file.Files.readBytesAndMimeTypeFromUri
import com.sayler666.gina.core.flow.Event
import com.sayler666.gina.dayDetails.ui.FilePreview
import com.sayler666.gina.dayDetails.ui.ImagePreview
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.dayDetails.viewmodel.AttachmentEntity.NonImage
import com.sayler666.gina.dayDetails.viewmodel.DayWithAttachmentsEntity
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.FullImageDestination
import com.sayler666.gina.ui.DatePicker


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
        handleSelectedFiles(it, context, viewModel)
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
    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }
    DeleteConfirmationDialog(showDeleteConfirmationDialog) { viewModel.removeDay() }
    DiscardConfirmationDialog(showDiscardConfirmationDialog) { navController.popBackStack() }

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
                    onDateButtonClicked = { date -> viewModel.setNewDate(date) }
                )
            }
        },
        bottomBar = { BottomBar(showDeleteConfirmationDialog, viewModel, addAttachmentLauncher) },
        content = { padding ->
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
                    Row(
                        modifier = Modifier
                            .padding(16.dp, 8.dp)
                            .fillMaxWidth()
                    ) {
                        BasicTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = it.content,
                            onValueChange = viewModel::setNewContent,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        })
}

private fun handleBackPress(
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

@Composable
private fun Attachments(
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
private fun SaveFab(onSaveButtonClicked: () -> Unit) {
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
    showDeleteConfirmationDialog: MutableState<Boolean>,
    viewModel: DayDetailsEditViewModel,
    addAttachmentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
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
            IconButton(onClick = { /* Do Something */ }) {
                Icon(
                    painter = rememberVectorPainter(image = Filled.SentimentNeutral),
                    contentDescription = null,
                )
            }
        },
        floatingActionButton = { SaveFab { viewModel.saveChanges() } }
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
                Button(
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
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = { showDialog.value = false }
                ) { Text("Cancel") }
            }
        )
    }
}

private fun handleSelectedFiles(
    it: ActivityResult,
    context: Context,
    viewModel: DayDetailsEditViewModel
) {

    fun addAttachment(uri: Uri) {
        val (content, mimeType) = readBytesAndMimeTypeFromUri(uri, context)
        viewModel.addAttachment(content, mimeType)
    }

    if (it.resultCode != RESULT_CANCELED && it.data != null) {
        // multiple files
        val multipleItems = it.data?.clipData
        if (multipleItems != null) {
            for (i in 0 until multipleItems.itemCount) {
                addAttachment(multipleItems.getItemAt(i).uri)
            }
        } else {
            // single file
            it.data?.data?.let { uri ->
                addAttachment(uri)
            }
        }
    }
}
