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
    databaseSize: Long?,
    onNewDbFileSelected: (String) -> Unit,
    onLongPress: () -> Unit,
    loader: Boolean,
) {
    val databaseResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.path?.let { path -> onNewDbFileSelected(path) }
        }
    val body = buildString {
        databasePath?.let { append(it) }
        databaseSize?.let {
            if (isNotEmpty()) append("\n")
            append(formatBinarySize(it))
        }
    }
    SettingsButton(
        header = stringResource(R.string.settings_database_file),
        body = body,
        icon = Icons.Filled.Book,
        onClick = { databaseResult.launch(Files.selectFileIntent()) },
        onLongClick = onLongPress,
        loader = loader
    )
}

private fun formatBinarySize(bytes: Long): String = when {
    bytes >= 1024L * 1024 * 1024 -> "%.1f GB".format(bytes / (1024.0 * 1024 * 1024))
    bytes >= 1024L * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024))
    bytes >= 1024L -> "%.1f KB".format(bytes / 1024.0)
    else -> "$bytes B"
}
