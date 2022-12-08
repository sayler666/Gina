package com.sayler666.gina.selectdatabase

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sayler666.gina.R.string.select_database_grant_permission
import com.sayler666.gina.R.string.select_database_open_database
import com.sayler666.gina.file.Files
import com.sayler666.gina.permission.Permissions
import com.sayler666.gina.selectdatabase.viewmodel.SelectDatabaseViewModel

@Destination(start = true)
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun SelectDatabaseScreen(
    destinationsNavigator: DestinationsNavigator,
    viewModel: SelectDatabaseViewModel = hiltViewModel()
) {
    viewModel.attachDestinationsNavigator(destinationsNavigator)

    val permissionGranted: Boolean by viewModel.permissionGranted.collectAsStateWithLifecycle()
    val databaseResult = rememberLauncherForActivityResult(StartActivityForResult()) {
        it.data?.data?.path?.let { path -> viewModel.openDatabase(path) }
    }
    val permissionsResult = rememberLauncherForActivityResult(StartActivityForResult()) {
        viewModel.refreshPermissionStatus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (permissionGranted.not()) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd),
                shape = MaterialTheme.shapes.extraLarge,
                onClick = {
                    permissionsResult.launch(Permissions.getManageAllFilesSettingsIntent())
                },
            ) {
                Text(
                    style = typography.labelLarge,
                    text = stringResource(select_database_grant_permission)
                )
            }
        } else {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd),
                shape = MaterialTheme.shapes.extraLarge,
                onClick = {
                    databaseResult.launch(Files.selectFileIntent())
                },
            ) {
                Text(
                    style = typography.labelLarge,
                    text = stringResource(select_database_open_database)
                )
            }
        }
    }

}
