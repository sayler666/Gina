package com.sayler666.gina.settings.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.core.compose.plus
import com.sayler666.core.file.Files
import com.sayler666.core.image.ImageOptimization.OptimizationSettings
import com.sayler666.gina.destinations.ManageFriendsScreenDestination
import com.sayler666.gina.ginaApp.BOTTOM_NAV_HEIGHT
import com.sayler666.gina.ginaApp.viewModel.GinaMainViewModel
import com.sayler666.gina.reminder.viewmodel.ReminderEntity
import com.sayler666.gina.settings.viewmodel.SettingsViewModel
import com.sayler666.gina.settings.viewmodel.ThemeItem
import com.sayler666.gina.ui.NavigationBarColor
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@RootNavGraph
@Destination
@Composable
fun SettingsScreen(
    destinationsNavigator: DestinationsNavigator,
    viewModel: SettingsViewModel = hiltViewModel(),
    vm: GinaMainViewModel = hiltViewModel(),
) {

    val theme by vm.theme.collectAsStateWithLifecycle()
    NavigationBarColor(theme = theme)
    val context = LocalContext.current
    val imageOptimizationSettings: OptimizationSettings? by viewModel.imageOptimizationVM.imageOptimizationSettings.collectAsStateWithLifecycle()
    val databasePath: String? by viewModel.databasePath.collectAsStateWithLifecycle()
    val themes: List<ThemeItem> by viewModel.themes.collectAsStateWithLifecycle()
    val reminder: ReminderEntity by viewModel.remindersVM.reminder.collectAsStateWithLifecycle()
    val dbCardLoader: Boolean by viewModel.showDbCardLoader.collectAsStateWithLifecycle()

    val notificationPermissionState =
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest {
            it?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                    .verticalScroll(rememberScrollState())
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
                    },
                    onLongPress = {
                        viewModel.vacuumDatabase()
                    },
                    loader = dbCardLoader
                )
                FriendsSettingsSections(destinationsNavigator)
                Text(
                    text = "Attachments",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ImageCompressSettingsSection(
                    imageOptimizationSettings,
                    onSetImageQuality = {
                        viewModel.imageOptimizationVM.setNewImageQuality(it)
                    },
                    onImageCompressionToggled = viewModel.imageOptimizationVM::toggleImageCompression
                )
                Text(
                    text = "Personalize",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ThemesSettingsSections(themes) { theme ->
                    viewModel.setTheme(theme)
                }
                ReminderSettingsSections(
                    currentReminder = reminder,
                    onReminderSet = { time ->
                        viewModel.remindersVM.setReminder(time)
                        if (!notificationPermissionState.status.isGranted) {
                            notificationPermissionState.launchPermissionRequest()
                        }
                    },
                    onReminderCancel = viewModel.remindersVM::removeReminders
                )
                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(
                        WindowInsets.systemBars + WindowInsets(bottom = BOTTOM_NAV_HEIGHT)
                    )
                )
            }
        })
}

@Composable
private fun DatabaseSettingsSection(
    databasePath: String?,
    onNewDbFileSelected: (String) -> Unit,
    onLongPress: () -> Unit,
    loader: Boolean,
) {
    val databaseResult = rememberLauncherForActivityResult(StartActivityForResult()) {
        it.data?.data?.path?.let { path -> onNewDbFileSelected(path) }
    }
    SettingsButton(
        header = "Database file",
        body = databasePath ?: "",
        icon = Filled.Book,
        onClick = { databaseResult.launch(Files.selectFileIntent()) },
        onLongClick = onLongPress,
        loader = loader
    )
}

@Composable
private fun FriendsSettingsSections(
    destinationsNavigator: DestinationsNavigator
) {
    SettingsButton(
        header = "Friends",
        body = "Manage friends list",
        icon = Filled.People,
        onClick = {
            destinationsNavigator.navigate(ManageFriendsScreenDestination)
        }
    )
}
