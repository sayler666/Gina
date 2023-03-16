package com.sayler666.gina.settings.ui

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.sayler666.gina.core.file.Files
import com.sayler666.gina.dayDetails.viewmodel.FriendEntity
import com.sayler666.gina.imageCompressor.ImageCompressor.CompressorSettings
import com.sayler666.gina.settings.viewmodel.SettingsViewModel
import com.sayler666.gina.ui.FriendsPicker

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val imageCompressorSettings: CompressorSettings? by viewModel.imageCompressorSettings.collectAsStateWithLifecycle()
    val databasePath: String? by viewModel.databasePath.collectAsStateWithLifecycle()
    val friends: List<FriendEntity> by viewModel.friends.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Database",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                DatabaseSettingsSection(
                    databasePath,
                    onNewDbFileSelected = { path ->
                        viewModel.openDatabase(path)
                    }
                )
                FriendsSettingsSections(
                    friends,
                    onAddNewFriend = { viewModel.addNewFriend(it) },
                    onSearchChanged = { viewModel.searchFriend(it) },
                    onFriendClicked = { id ->
                        // TODO
                    }
                )
                Text(
                    text = "Attachments",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ImageCompressSettingsSection(
                    imageCompressorSettings,
                    onSetImageQuality = viewModel::setNewImageQuality,
                    onSetImageSize = viewModel::setNewImageSize
                )
            }
        })
}

@Composable
private fun DatabaseSettingsSection(
    databasePath: String?,
    onNewDbFileSelected: (String) -> Unit
) {
    val databaseResult = rememberLauncherForActivityResult(StartActivityForResult()) {
        it.data?.data?.path?.let { path -> onNewDbFileSelected(path) }
    }
    SettingsButton(
        header = "Database file",
        body = databasePath ?: "",
        icon = Filled.Book,
        onClick = {
            databaseResult.launch(Files.selectFileIntent())
        }
    )
}

@Composable
private fun FriendsSettingsSections(
    friends: List<FriendEntity>,
    onSearchChanged: (String) -> Unit,
    onAddNewFriend: (String) -> Unit,
    onFriendClicked: (Int) -> Unit,
) {
    val searchQuery = rememberSaveable { mutableStateOf("") }
    val showFriendsPopup = remember { mutableStateOf(false) }
    SettingsButton(
        header = "Friends",
        body = "Manage friends list",
        icon = Filled.People,
        onClick = {
            showFriendsPopup.value = true
        }
    )

    FriendsPicker(
        showPopup = showFriendsPopup.value,
        friends = friends,
        searchValue = searchQuery.value,
        onDismiss = {
            showFriendsPopup.value = false
            searchQuery.value = ""
            onSearchChanged("")
        },
        onSearchChanged = {
            searchQuery.value = it
            onSearchChanged(it)

        },
        onAddNewFriend = {
            onAddNewFriend(it)
            searchQuery.value = ""
            onSearchChanged("")
        },
        onFriendClicked = { id, _ -> onFriendClicked(id) },
        selectable = false
    )
}

@Composable
private fun ImageCompressSettingsSection(
    imageCompressorSettings: CompressorSettings?,
    onSetImageQuality: (Int) -> Unit,
    onSetImageSize: (Long) -> Unit,
) {
    val showImageCompressSettingsDialog = remember { mutableStateOf(false) }
    imageCompressorSettings?.let {
        SettingsButton(
            header = "Image optimization",
            body = "Quality: ${it.quality}%, Size: ${it.size / 1000}KB",
            icon = Filled.PhotoSizeSelectLarge,
            onClick = { showImageCompressSettingsDialog.value = true }
        )
        ImageCompressSettingsDialog(
            showDialog = showImageCompressSettingsDialog.value,
            imageCompressorSettings = imageCompressorSettings,
            onDismiss = { showImageCompressSettingsDialog.value = false },
            onSetImageQuality = onSetImageQuality,
            onSetImageSize = onSetImageSize
        )
    }
}

@Composable
private fun SettingsButton(
    header: String,
    body: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .clip(shape = CircleShape)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Icon(icon, null, Modifier.padding(10.dp))
            }
            Column(modifier = Modifier.padding(0.dp)) {
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelLarge
                        .copy(color = MaterialTheme.colorScheme.onBackground)
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.labelMedium
                        .copy(color = MaterialTheme.colorScheme.outline)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Filled.ChevronRight, null, Modifier.padding(end = 8.dp))
        }
    }
}

@Composable
fun ImageCompressSettingsDialog(
    showDialog: Boolean,
    imageCompressorSettings: CompressorSettings?,
    onDismiss: () -> Unit,
    onSetImageQuality: (Int) -> Unit,
    onSetImageSize: (Long) -> Unit,
) {
    if (showDialog) {
        imageCompressorSettings?.let {
            Dialog(onDismissRequest = { onDismiss() }) {
                Card(Modifier.padding(8.dp)) {
                    Column {
                        var qualitySliderPosition by remember { mutableStateOf(it.quality.toFloat()) }
                        Row(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Quality:",
                                style = MaterialTheme.typography.labelLarge
                                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                            Text(
                                text = " ${qualitySliderPosition.toInt()}%",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.W900)
                            )
                        }
                        Slider(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            value = qualitySliderPosition,
                            valueRange = 1f..100f,
                            steps = 100,
                            onValueChange = { value: Float ->
                                qualitySliderPosition = value
                            },
                            onValueChangeFinished = {
                                onSetImageQuality(qualitySliderPosition.toInt())
                            })

                        var sizeSliderPosition by remember { mutableStateOf(it.size.toFloat()) }
                        Row(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Size:",
                                style = MaterialTheme.typography.labelLarge
                                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                            Text(
                                text = " ${sizeSliderPosition.toInt() / 1000}KB",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.W500)
                            )
                        }
                        Slider(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            value = sizeSliderPosition,
                            valueRange = 1f..500_000f,
                            steps = 100,
                            onValueChange = { value: Float ->
                                sizeSliderPosition = value
                            },
                            onValueChangeFinished = {
                                onSetImageSize(sizeSliderPosition.toLong())
                            })
                    }
                }
            }
        }
    }
}
