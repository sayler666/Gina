package com.sayler666.gina.feature.settings.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.compose.plus
import com.sayler666.core.file.Files
import com.sayler666.gina.feature.settings.viewmodel.SettingsState
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.Back
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.NavToManageFriends
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.ShowToast
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnDatabaseFileSelected
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnImageCompressionToggled
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnImageQualityChanged
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnIncognitoModeToggled
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnManageFriendsPressed
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnReminderCancel
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnReminderSet
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnThemeSelected
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnVacuumDatabasePressed
import com.sayler666.gina.feature.settings.reminder.NotActive
import com.sayler666.gina.navigation.Route
import com.sayler666.gina.ui.LocalNavigator
import com.sayler666.gina.ui.hideNavBar.BOTTOM_NAV_HEIGHT

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val notificationPermissionState =
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    BackHandler { viewModel.onViewEvent(OnBackPressed) }

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            Back -> navigator.back()
            NavToManageFriends -> navigator.navigate(Route.ManageFriends)
            is ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
        }
    }

    Content(
        state = viewState,
        viewEvent = viewModel::onViewEvent,
        notificationPermissionState = notificationPermissionState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun Content(
    state: SettingsState?,
    viewEvent: (ViewEvent) -> Unit,
    notificationPermissionState: com.google.accompanist.permissions.PermissionState,
) {
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
                Spacer(Modifier.padding(top = 16.dp))
                SettingsSectionHeader("Database")
                DatabaseSettingsButtonWithLauncher(
                    databasePath = state?.databasePath,
                    onNewDbFileSelected = { path ->
                        viewEvent(OnDatabaseFileSelected(path))
                    },
                    onLongPress = {
                        viewEvent(OnVacuumDatabasePressed)
                    },
                    loader = state?.showDbCardLoader ?: false
                )
                SettingsButton(
                    header = "Friends",
                    body = "Manage friends list",
                    icon = Filled.People,
                    onClick = { viewEvent(OnManageFriendsPressed) }
                )
                SettingsSectionHeader("Attachments")
                ImageCompressSettingsSection(
                    state?.imageOptimizationSettings,
                    onSetImageQuality = { quality ->
                        viewEvent(OnImageQualityChanged(quality))
                    },
                    onImageCompressionToggled = { enabled ->
                        viewEvent(OnImageCompressionToggled(enabled))
                    }
                )
                SettingsSectionHeader("Personalize")
                ThemesSettingsSections(state?.themes ?: emptyList()) { theme ->
                    viewEvent(OnThemeSelected(theme))
                }
                ReminderSettingsSections(
                    currentReminder = state?.reminderState ?: NotActive,
                    onReminderSet = { time ->
                        viewEvent(OnReminderSet(time))
                        if (!notificationPermissionState.status.isGranted) {
                            notificationPermissionState.launchPermissionRequest()
                        }
                    },
                    onReminderCancel = {
                        viewEvent(OnReminderCancel)
                    }
                )
                SettingsButton(
                    header = "Incognito Mode",
                    body = if (state?.incognitoMode == true) "Scrambles text content in screenshots" else "Off",
                    icon = Filled.Visibility,
                    onClick = { viewEvent(OnIncognitoModeToggled(!(state?.incognitoMode ?: false))) },
                    checked = state?.incognitoMode ?: false,
                    onCheckedChange = { viewEvent(OnIncognitoModeToggled(it)) }
                )
                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(
                        WindowInsets.systemBars + WindowInsets(bottom = BOTTOM_NAV_HEIGHT*2)
                    )
                )
            }
        })
}

@Composable
private fun DatabaseSettingsButtonWithLauncher(
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
