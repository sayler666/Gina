@file:OptIn(ExperimentalPermissionsApi::class)

package com.sayler666.gina.feature.settings.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.compose.plus
import com.sayler666.core.compose.scroll.rememberScrollConnection
import com.sayler666.gina.feature.settings.viewmodel.SettingsState
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.Back
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.CreateNewDatabase
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.ExportDatabase
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.OpenDatabase
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.NavToManageFriends
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.RequestNotificationPermission
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.RestartApp
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewAction.ShowToast
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnBackPressed
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnCreateNewDatabasePressed
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnDatabaseFileSelected
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnExportDatabasePressed
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnExportDatabaseRequested
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnHideBottomBar
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnImportDatabasePressed
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnIncognitoModeToggled
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnManageFriendsPressed
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnNewDatabaseCreated
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnReminderCancel
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnReminderSet
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnShowBottomBar
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnThemeSelected
import com.sayler666.gina.feature.settings.viewmodel.SettingsViewModel.ViewEvent.OnVacuumDatabasePressed
import com.sayler666.gina.navigation.Navigator
import com.sayler666.gina.navigation.routes.ManageFriends
import com.sayler666.gina.reminders.viewmodel.NotActive
import com.sayler666.gina.resources.R
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

    val openDocumentLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri: Uri? ->
        uri?.let { viewModel.onViewEvent(OnDatabaseFileSelected(it)) }
    }
    val createDocumentLauncher =
        rememberLauncherForActivityResult(CreateDocument("application/vnd.sqlite3")) { uri: Uri? ->
            uri?.let { viewModel.onViewEvent(OnNewDatabaseCreated(it)) }
        }
    val exportLauncher =
        rememberLauncherForActivityResult(CreateDocument("application/vnd.sqlite3")) { uri: Uri? ->
            uri?.let { viewModel.onViewEvent(OnExportDatabaseRequested(it)) }
        }

    LifecycleResumeEffect(Unit) {
        viewModel.onViewEvent(OnHideBottomBar)
        onPauseOrDispose {
            viewModel.onViewEvent(OnShowBottomBar)
        }
    }

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        onViewActions(
            action = action,
            navigator = navigator,
            context = context,
            notificationPermissionState = notificationPermissionState,
            openDocumentLauncher = openDocumentLauncher,
            createDocumentLauncher = createDocumentLauncher,
            exportLauncher = exportLauncher
        )
    }

    BackHandler { viewModel.onViewEvent(OnBackPressed) }

    Content(state = viewState, viewEvent = viewModel::onViewEvent)
}

private fun onViewActions(
    action: ViewAction,
    navigator: Navigator,
    context: Context,
    notificationPermissionState: PermissionState,
    openDocumentLauncher: ManagedActivityResultLauncher<Array<String>, Uri?>,
    createDocumentLauncher: ManagedActivityResultLauncher<String, Uri?>,
    exportLauncher: ManagedActivityResultLauncher<String, Uri?>,
) {
    when (action) {
        Back -> navigator.back()
        NavToManageFriends -> navigator.navigate(ManageFriends)
        is ShowToast -> Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
        RestartApp -> restartApp(context)
        OpenDatabase -> openDocumentLauncher.launch(arrayOf("*/*"))
        CreateNewDatabase -> createDocumentLauncher.launch("gina_journal.db")
        ExportDatabase -> exportLauncher.launch("gina_journal_backup.db")
        RequestNotificationPermission -> {
            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }
}

private fun restartApp(context: Context) {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        ?.apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) }
    context.startActivity(intent)
    Runtime.getRuntime().exit(0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: SettingsState?,
    viewEvent: (ViewEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = { Text(stringResource(R.string.settings)) }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.padding(top = 16.dp))
                SettingsSectionHeader(stringResource(R.string.settings_section_database))
                DatabaseSettingsSection(
                    databaseExternalPath = state?.databaseExternalPath,
                    databaseSize = state?.databaseSize,
                    onImportDatabasePressed = { viewEvent(OnImportDatabasePressed) },
                    onCreateNewDatabasePressed = { viewEvent(OnCreateNewDatabasePressed) },
                    onExportDatabasePressed = { viewEvent(OnExportDatabasePressed) },
                    onLongPress = { viewEvent(OnVacuumDatabasePressed) },
                    loader = state?.showDbCardLoader ?: false
                )
                SettingsButton(
                    header = stringResource(R.string.settings_friends),
                    body = stringResource(R.string.settings_manage_friends_list),
                    icon = Filled.People,
                    onClick = { viewEvent(OnManageFriendsPressed) }
                )
                SettingsSectionHeader(stringResource(R.string.settings_section_attachments))
                ImageOptimizationSettingsSection()
                SettingsSectionHeader(stringResource(R.string.settings_section_personalize))
                ThemesSettingsSections(state?.themes ?: emptyList()) { theme ->
                    viewEvent(OnThemeSelected(theme))
                }
                ReminderSettingsSections(
                    currentReminder = state?.reminderState ?: NotActive,
                    onReminderSet = { time -> viewEvent(OnReminderSet(time)) },
                    onReminderCancel = { viewEvent(OnReminderCancel) }
                )
                SettingsButton(
                    header = stringResource(R.string.settings_incognito_mode),
                    body = if (state?.incognitoMode == true) stringResource(R.string.settings_incognito_enabled) else stringResource(
                        R.string.settings_incognito_disabled
                    ),
                    icon = if (state?.incognitoMode == true) Filled.VisibilityOff else Filled.Visibility,
                    onClick = {
                        viewEvent(
                            OnIncognitoModeToggled(
                                !(state?.incognitoMode ?: false)
                            )
                        )
                    },
                    checked = state?.incognitoMode ?: false,
                    onCheckedChange = { viewEvent(OnIncognitoModeToggled(it)) }
                )
                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(
                        WindowInsets.systemBars + WindowInsets(bottom = BOTTOM_NAV_HEIGHT)
                    )
                )
            }
        }
    )
}
