package com.sayler666.gina.selectdatabase.ui

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.core.file.Files
import com.sayler666.core.permission.Permissions
import com.sayler666.gina.navigation.Journal
import com.sayler666.gina.resources.R.string.select_database_grant_permission
import com.sayler666.gina.resources.R.string.select_database_open_database
import com.sayler666.gina.selectdatabase.viewmodel.SelectDatabaseViewModel
import com.sayler666.gina.ui.LocalNavigator
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SelectDatabaseScreen(
    viewModel: SelectDatabaseViewModel = hiltViewModel()
) {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    val permissionGranted: Boolean by viewModel.permissionGranted.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToHome.collectLatest { navigator.navigateToRoot(Journal) }
    }

    val databaseResult =
        rememberLauncherForActivityResult(contract = StartActivityForResult()) { result ->
            result.data?.data?.path?.let { path -> viewModel.openDatabase(path) }
        }
    val permissionsResult = rememberLauncherForActivityResult(contract = StartActivityForResult()) {
        viewModel.refreshPermissionStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (permissionGranted.not()) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                onClick = { permissionsResult.launch(Permissions.getManageAllFilesSettingsIntent(context)) },
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
                onClick = { databaseResult.launch(Files.selectFileIntent()) },
            ) {
                Text(
                    style = typography.labelLarge,
                    text = stringResource(select_database_open_database)
                )
            }
        }
    }
}
