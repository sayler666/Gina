package com.sayler666.gina.feature.settings.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.sayler666.gina.resources.R

@Composable
internal fun DatabaseSettingsSection(
    databaseExternalPath: String?,
    databaseSize: Long?,
    onImportDatabasePressed: () -> Unit,
    onCreateNewDatabasePressed: () -> Unit,
    onExportDatabasePressed: () -> Unit,
    onLongPress: () -> Unit,
    loader: Boolean,
) {
    var showPickerDialog by remember { mutableStateOf(false) }

    if (showPickerDialog) {
        AlertDialog(
            onDismissRequest = { showPickerDialog = false },
            title = { Text(stringResource(R.string.settings_database_file)) },
            confirmButton = {
                TextButton(onClick = {
                    showPickerDialog = false
                    onImportDatabasePressed()
                }) { Text(stringResource(R.string.settings_database_open_existing)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPickerDialog = false
                    onCreateNewDatabasePressed()
                }) { Text(stringResource(R.string.settings_database_create_new)) }
            }
        )
    }

    val body = buildString {
        databaseExternalPath?.let { append(it) }
        databaseSize?.let {
            if (isNotEmpty()) append("\n")
            append(formatBinarySize(it))
        }
    }
    SettingsButton(
        header = stringResource(R.string.settings_database_file),
        body = body,
        icon = Icons.Filled.Book,
        onClick = { showPickerDialog = true },
        onLongClick = onLongPress,
        loader = loader
    )
    SettingsButton(
        header = stringResource(R.string.settings_export_database),
        body = "",
        icon = Icons.Filled.Upload,
        onClick = { onExportDatabasePressed() }
    )
}

private fun formatBinarySize(bytes: Long): String = when {
    bytes >= 1024L * 1024 * 1024 -> "%.1f GB".format(bytes / (1024.0 * 1024 * 1024))
    bytes >= 1024L * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024))
    bytes >= 1024L -> "%.1f KB".format(bytes / 1024.0)
    else -> "$bytes B"
}
