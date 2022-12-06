package com.sayler666.gina.dayslist.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.gina.dayslist.viewmodel.DaysViewModel
import com.sayler666.gina.db.Days
import com.sayler666.gina.file.Files
import com.sayler666.gina.permission.Permissions

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun Days(viewModel: DaysViewModel = hiltViewModel()) {
    val permissionGranted: Boolean by viewModel.permissionGranted.collectAsStateWithLifecycle()
    val days: List<Days> by viewModel.days.collectAsStateWithLifecycle()
    val databaseResult = rememberLauncherForActivityResult(StartActivityForResult()) {
        it.data?.data?.path?.let { path -> viewModel.openDatabase(path) }
    }
    val permissionsResult = rememberLauncherForActivityResult(StartActivityForResult()) {
        viewModel.refreshPermissionStatus()
    }

    Row(modifier = Modifier.padding(16.dp)) {
        if (permissionGranted.not()) {
            Button(
                onClick = {
                    permissionsResult.launch(Permissions.getManageAllFilesSettingsIntent())
                },
            ) {
                Text("Grant permission")
            }
        } else {
            Button(
                onClick = { databaseResult.launch(Files.selectFileIntent()) },
            ) {
                Text("Open database")
            }
        }
        Text(
            text = "Records: ${days.size}",
            fontSize = 16.sp
        )
    }
}
