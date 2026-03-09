package com.sayler666.gina.feature.settings.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sayler666.core.file.Files
import com.sayler666.gina.resources.R

@Composable
internal fun DatabaseSettingsButtonWithLauncher(
    databasePath: String?,
    onNewDbFileSelected: (String) -> Unit,
    onLongPress: () -> Unit,
    loader: Boolean,
) {
    val databaseResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.path?.let { path -> onNewDbFileSelected(path) }
        }
    SettingsButton(
        header = stringResource(R.string.settings_database_file),
        body = databasePath ?: "",
        icon = Icons.Filled.Book,
        onClick = { databaseResult.launch(Files.selectFileIntent()) },
        onLongClick = onLongPress,
        loader = loader
    )
}
