package com.sayler666.gina.feature.setup.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.core.file.Files
import com.sayler666.core.permission.Permissions
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewAction
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewEvent
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewState
import com.sayler666.gina.navigation.routes.Journal
import com.sayler666.gina.resources.R.string.select_database_grant_permission
import com.sayler666.gina.resources.R.string.select_database_open_database
import com.sayler666.gina.ui.LocalNavigator

@Composable
fun SetupScreen(viewModel: SetupViewModel = hiltViewModel()) {
    val viewState: ViewState by viewModel.viewState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            ViewAction.NavigateToJournal -> navigator.navigateToRoot(Journal)
        }
    }

    Content(state = viewState, viewEvent = viewModel::onViewEvent)
}

@Composable
private fun Content(state: ViewState, viewEvent: (ViewEvent) -> Unit) {
    val context = LocalContext.current

    val databaseResult = rememberLauncherForActivityResult(StartActivityForResult()) { result ->
        result.data?.data?.path?.let { path -> viewEvent(ViewEvent.OnDatabaseSelected(path)) }
    }
    val permissionResult = rememberLauncherForActivityResult(StartActivityForResult()) {
        viewEvent(ViewEvent.OnPermissionResult)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (!state.permissionGranted) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                onClick = { permissionResult.launch(Permissions.getManageAllFilesSettingsIntent(context)) }
            ) {
                Text(
                    style = typography.labelLarge,
                    text = stringResource(select_database_grant_permission)
                )
            }
        } else {
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                onClick = { databaseResult.launch(Files.selectFileIntent()) }
            ) {
                Text(
                    style = typography.labelLarge,
                    text = stringResource(select_database_open_database)
                )
            }
        }
    }
}
