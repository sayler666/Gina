package com.sayler666.gina.ui.dialog

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.LocalHapticFeedbackManager
import com.sayler666.gina.ui.LongPressButton
import com.sayler666.gina.ui.filters.MoodFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    text: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onConfirmAction: () -> Unit
) {
    val haptics = LocalHapticFeedbackManager.current
    val context = LocalContext.current
    if (showDialog.value) {
        BasicAlertDialog(
            onDismissRequest = { showDialog.value = false }
        ) {
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.large,
                            onClick = { showDialog.value = false }
                        ) { Text(dismissButtonText) }

                        Spacer(modifier = Modifier.width(8.dp))

                        LongPressButton(
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.large,
                            onShortPress = {
                                Toast.makeText(
                                    context,
                                    R.string.hold_to_confirm,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onClick = {
                                showDialog.value = false
                                haptics.dayRemoved()
                                onConfirmAction()
                            }
                        ) { Text(confirmButtonText) }
                    }
                }
            }
        }
    }
}
