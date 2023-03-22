package com.sayler666.gina.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun ConfirmationDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    text: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onConfirmAction: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                        showDialog.value = false
                        onConfirmAction()
                    }
                ) { Text(confirmButtonText) }
            },
            dismissButton = {
                OutlinedButton(
                    shape = MaterialTheme.shapes.medium,
                    onClick = { showDialog.value = false }
                ) { Text(dismissButtonText) }
            }
        )
    }
}
