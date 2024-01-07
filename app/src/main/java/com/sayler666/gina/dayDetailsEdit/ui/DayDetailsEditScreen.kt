package com.sayler666.gina.dayDetailsEdit.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.PickVisualMediaRequest.Builder
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.file.Files.openFileIntent
import com.sayler666.core.file.handleMultipleVisualMedia
import com.sayler666.core.image.ImageOptimization
import com.sayler666.gina.attachments.ui.FileThumbnail
import com.sayler666.gina.attachments.ui.ImagePreviewTmpScreenNavArgs
import com.sayler666.gina.attachments.ui.ImageThumbnail
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity.NonImage
import com.sayler666.gina.calendar.ui.DatePickerDialog
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsEntity
import com.sayler666.gina.dayDetailsEdit.viewmodel.DayDetailsEditViewModel
import com.sayler666.gina.destinations.DayDetailsScreenDestination
import com.sayler666.gina.destinations.ImagePreviewTmpScreenDestination
import com.sayler666.gina.destinations.JournalScreenDestination
import com.sayler666.gina.friends.ui.FriendIcon
import com.sayler666.gina.friends.ui.FriendsPicker
import com.sayler666.gina.friends.viewmodel.FriendEntity
import com.sayler666.gina.settings.ui.ImageCompressBottomSheet
import com.sayler666.gina.ui.DayTitle
import com.sayler666.gina.ui.VerticalDivider
import com.sayler666.gina.ui.dialog.ConfirmationDialog
import com.sayler666.gina.ui.keyboardAsState
import com.sayler666.gina.ui.richeditor.RichTextEditor
import com.sayler666.gina.ui.richeditor.RichTextStyleRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.sayler666.gina.mood.Mood
import com.sayler666.gina.mood.ui.MoodIcon
import com.sayler666.gina.mood.ui.MoodPicker
import com.sayler666.gina.mood.ui.mapToMoodIcon
import kotlin.math.PI
import kotlin.math.sin


data class DayDetailsEditScreenNavArgs(
    val dayId: Int
)

@RootNavGraph
@Destination(navArgsDelegate = DayDetailsEditScreenNavArgs::class)
@Composable
fun DayDetailsEditScreen(
    destinationsNavigator: DestinationsNavigator,
    navController: NavController,
    viewModel: DayDetailsEditViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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
            destinationsNavigator.popBackStack(
                route = DayDetailsScreenDestination,
                inclusive = true
            )
        }
    }

    val showDeleteConfirmationDialog = rememberConfirmationDialog(viewModel)
    val showDiscardConfirmationDialog = rememberDiscardDialog(navController)
    val showDatePickerPopup = remember { mutableStateOf(false) }

    fun onBackPress() {
        handleBackPress(
            changesExist,
            showDiscardConfirmationDialog,
            navController
        )
    }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collectLatest { destinationsNavigator.popBackStack() }
    }

    val imageOptimizationSettings: ImageOptimization.OptimizationSettings? by viewModel.imageOptimizationSettings.collectAsStateWithLifecycle()
    val showImageCompressSettingsDialog = remember { mutableStateOf(false) }
    ImageCompressBottomSheet(
        showDialog = showImageCompressSettingsDialog.value,
        imageOptimizationSettings = imageOptimizationSettings,
        onDismiss = { showImageCompressSettingsDialog.value = false },
        onSetImageQuality = viewModel::setNewImageQuality,
        onImageCompressionToggled = viewModel::toggleImageCompression
    )

    val isKeyboardOpen by keyboardAsState()
    val richTextState = rememberRichTextState()
    val showFormatRow = remember { mutableStateOf(false) }

    BackHandler(onBack = ::onBackPress)

    Scaffold(
        Modifier.imePadding(),
        topBar = {
            currentDay?.let { day ->
                TopBar(
                    day = day,
                    onNavigateBackClicked = ::onBackPress,
                    onChangeDateClicked = {
                        showDatePickerPopup.value = true
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
                            richTextState = richTextState,
                            text = day.content,
                            onContentChanged = viewModel::setNewContent
                        )
                    }
                }
            }
        })
}

@Composable
fun rememberLauncherForMultipleImages(
    context: Context,
    onResult: (List<Pair<ByteArray, String>>) -> Unit
) = rememberLauncherForActivityResult(PickMultipleVisualMedia()) {
    handleMultipleVisualMedia(it, context) { attachments ->
        onResult(attachments)
    }
}

@Composable
private fun rememberDiscardDialog(navController: NavController): MutableState<Boolean> {
    val showDiscardConfirmationDialog = remember { mutableStateOf(false) }
    ConfirmationDialog(
        title = "Discard changes",
        text = "Do you really want to discard changes?",
        confirmButtonText = "Discard",
        dismissButtonText = "Cancel",
        showDialog = showDiscardConfirmationDialog,
    ) { navController.popBackStack() }
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

@Composable
fun AttachmentsAmountLabel(
    attachments: List<AttachmentEntity>
) {
    Text(
        text = "Attachments: ${attachments.size}",
        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary),
        modifier = Modifier.padding(start = 16.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Attachments(
    day: DayDetailsEntity,
    destinationsNavigator: DestinationsNavigator,
    onRemoveAttachment: (Int) -> Unit
) {
    val context = LocalContext.current
    if (day.attachments.isNotEmpty()) FlowRow(modifier = Modifier.padding(16.dp, 0.dp)) {
        day.attachments.forEach { attachment ->
            when (attachment) {
                is Image -> ImageThumbnail(attachment, onClick = {
                    attachment.bytes.let { image ->
                        destinationsNavigator.navigate(
                            ImagePreviewTmpScreenDestination(
                                ImagePreviewTmpScreenNavArgs(
                                    image = image,
                                    mimeType = attachment.mimeType
                                )
                            )
                        )
                    }
                }, onRemoveClicked = {
                    onRemoveAttachment(attachment.bytes.hashCode())
                })

                is NonImage -> FileThumbnail(attachment, onClick = {
                    openFileIntent(
                        context, attachment.bytes, attachment.mimeType
                    )
                }, onRemoveClicked = {
                    onRemoveAttachment(attachment.bytes.hashCode())
                })
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBar(
    day: DayDetailsEntity,
    onNavigateBackClicked: () -> Unit,
    onChangeDateClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onChangeDateClicked()
                }
            ) {
                DayTitle(day.dayOfMonth, day.dayOfWeek, day.yearAndMonth)
                Icon(
                    Filled.ArrowDropDown,
                    tint = MaterialTheme.colorScheme.primary,
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
        onClick = onSaveButtonClicked
    ) {
        Icon(
            Filled.Save, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceVariant
        )
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

                Attachments(addAttachmentAction, onLongClick = addAttachmentLongClickAction)

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
            floatingActionButton = { SaveFab { onSaveChanges() } })
    }
}

@Composable
fun TextFormat(showFormat: MutableState<Boolean>) {
    IconButton(onClick = { showFormat.value = !showFormat.value }) {
        Icon(Filled.TextFormat, null)
    }
}

@Composable
private fun Delete(showDeleteConfirmationDialog: MutableState<Boolean>) {
    IconButton(onClick = { showDeleteConfirmationDialog.value = true }) {
        Icon(Filled.Delete, null)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Attachments(onClick: () -> Unit, onLongClick: (() -> Unit)? = null) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false),
                enabled = true,
                onLongClick = { onLongClick?.invoke() },
                onClick = onClick
            )
            .padding(start = 8.dp, end = 8.dp)
    ) {
        Icon(Filled.AddAPhoto, null)
    }
}

@Composable
fun Friends(
    friends: List<FriendEntity>,
    onSearchChanged: (String) -> Unit,
    onAddNewFriend: (String) -> Unit,
    onFriendClicked: (Int, Boolean) -> Unit,
    currentDay: DayDetailsEntity
) {
    val showFriendsPopup = remember { mutableStateOf(false) }

    when (friends.isNotEmpty()) {
        true -> Box(modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .clickable(
                indication = rememberRipple(bounded = false),
                interactionSource = remember {
                    MutableInteractionSource()
                }) { showFriendsPopup.value = true }) {
            friends.take(2).forEachIndexed { i, friend ->
                FriendIcon(
                    friend = friend,
                    size = 32.dp,
                    modifier = Modifier
                        .offset(i * 8.dp)
                        .zIndex(-i.toFloat())
                )
            }
        }

        false -> IconButton(onClick = { showFriendsPopup.value = true }) {
            Icon(
                painter = rememberVectorPainter(image = Filled.People),
                contentDescription = null,
            )
        }
    }

    val searchQuery = rememberSaveable { mutableStateOf("") }
    FriendsPicker(
        showFriendsPopup.value,
        searchValue = searchQuery.value,
        onDismiss = { showFriendsPopup.value = false },
        onSearchChanged = {
            searchQuery.value = it
            onSearchChanged(it)
        },
        onAddNewFriend = {
            onAddNewFriend(it)
            searchQuery.value = ""
            onSearchChanged(searchQuery.value)
        },
        onFriendClicked = onFriendClicked,
        friends = currentDay.friendsAll
    )
}

@Composable
fun Mood(
    currentDay: DayDetailsEntity,
    showMoodPopup: MutableState<Boolean>,
    onMoodChanged: (Mood) -> Unit
) {
    val scope = rememberCoroutineScope()
    val moodIcon: MoodIcon = currentDay.mood.mapToMoodIcon()

    var animationActive by remember { mutableStateOf(false) }
    val moodIconAnimParam by MoodIconAnimation().animateMoodIconAsState(
        mood = currentDay.mood,
        active = animationActive
    )

    IconButton(onClick = { showMoodPopup.value = true }) {
        Icon(
            modifier = Modifier
                .scale(scale = moodIconAnimParam.scale),
            painter = rememberVectorPainter(image = moodIcon.icon),
            tint = moodIcon.color,
            contentDescription = null,
        )
    }
    MoodPicker(showMoodPopup.value,
        onDismiss = { showMoodPopup.value = false },
        onSelectMood = { mood ->
            scope.launch {
                delay(60)
                showMoodPopup.value = false
            }
            animationActive = true
            onMoodChanged(mood)
        })
}

@Stable
data class MoodIconAnimParam(
    val scale: Float = 1f,
)

@Stable
class MoodIconAnimation(
    private val animationSpec: FiniteAnimationSpec<Float> = tween(250)
) {
    @Composable
    fun animateMoodIconAsState(
        mood: Mood?,
        active: Boolean
    ): State<MoodIconAnimParam> {
        val fraction = remember { Animatable(0f) }

        LaunchedEffect(mood, active) {
            if (active) {
                fraction.snapTo(0f)
                fraction.animateTo(1f, animationSpec)
            }
        }

        return produceState(
            initialValue = MoodIconAnimParam(),
            key1 = fraction.value
        ) {
            this.value = this.value.copy(
                scale = calculateScale(fraction.value),
            )
        }
    }

    private fun calculateScale(
        fraction: Float,
    ): Float = sin(PI * fraction).toFloat() * 0.7f + 1
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
