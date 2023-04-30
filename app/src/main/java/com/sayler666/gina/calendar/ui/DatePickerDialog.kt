package com.sayler666.gina.calendar.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.gina.calendar.viewmodel.CalendarDayEntity
import com.sayler666.gina.calendar.viewmodel.CalendarViewModel
import java.time.LocalDate

@Composable
fun DatePickerDialog(
    showPopup: Boolean,
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onDateChanged: (LocalDate) -> Unit,
) {
    val viewModel: CalendarViewModel = hiltViewModel()
    val days: List<CalendarDayEntity> by viewModel.days.collectAsStateWithLifecycle()
    val selectedDay = remember { mutableStateOf(initialDate) }

    if (showPopup) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                CalendarHorizontal(
                    days,
                    selectable = true,
                    selectedDate = initialDate,
                    onDayClick = { day ->
                        selectedDay.value = day.date
                    },
                    onEmptyDayClick = { date ->
                        selectedDay.value = date
                    },
                    firstVisible = initialDate
                )
                Row(Modifier.padding(14.dp)) {
                    OutlinedButton(
                        modifier = Modifier.weight(0.5f),
                        shape = MaterialTheme.shapes.medium,
                        onClick = { onDismiss() }
                    ) { Text("Cancel") }
                    Spacer(modifier = Modifier.weight(.025f))
                    Button(
                        modifier = Modifier.weight(0.5f),
                        shape = MaterialTheme.shapes.medium,
                        onClick = {
                            onDismiss()
                            onDateChanged(selectedDay.value)
                        }
                    ) { Text("Save") }
                }
            }
        }
    }
}
