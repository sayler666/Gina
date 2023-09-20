package com.sayler666.gina.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Rounded
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sayler666.gina.reminder.viewmodel.Active
import com.sayler666.gina.reminder.viewmodel.ReminderEntity
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingsSections(
    currentReminder: ReminderEntity,
    onReminderSet: (LocalTime) -> Unit,
    onReminderCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    SettingsButton(
        header = "Reminder",
        body = if (currentReminder is Active) currentReminder.time.format(ISO_LOCAL_TIME) else "Off",
        icon = if (currentReminder is Active) Filled.AlarmOn else Filled.AlarmOff,
        onClick = { showBottomSheet = true }
    )

    val initialPickerTime = if (currentReminder is Active) currentReminder.time else LocalTime.now()

    if (showBottomSheet) {
        val timePickerState = rememberTimePickerState(
            initialHour = initialPickerTime.hour,
            initialMinute = initialPickerTime.minute
        )
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            sheetState = sheetState,
            onDismissRequest = { scope.launch { showBottomSheet = false } },
        ) {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(bottom = 0.dp),
                title = {
                    Text("Reminder")
                }, actions = {
                    IconButton(onClick = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet = false
                        }
                    }) {
                        Icon(Rounded.Close, contentDescription = "Close")
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )

            TimePicker(
                state = timePickerState,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.Absolute.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                if (currentReminder is Active) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                onReminderCancel()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) showBottomSheet = false
                            }
                        }
                    ) { Text("Remove") }
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Button(
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onReminderSet(
                                LocalTime.of(timePickerState.hour, timePickerState.minute)
                            )
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet = false
                        }
                    }
                ) { Text(if (currentReminder is Active) "Update" else "Set") }
            }
        }
    }
}
