package com.sayler666.gina.calendar.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayler666.gina.calendar.viewmodel.DatePickerPopupViewModel
import com.sayler666.gina.core.date.displayText
import java.time.LocalDate
import java.time.YearMonth


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun DatePickerPopup(
    showPopup: Boolean,
    onDismiss: () -> Unit,
    dayPickerEnabled: Boolean = false,
    onSelectDate: (LocalDate) -> Unit,
    currentYearMonth: YearMonth
) {
    val viewModel = DatePickerPopupViewModel(currentYearMonth)
    val date: LocalDate by viewModel.date.collectAsStateWithLifecycle()

    if (showPopup) Popup(
        onDismissRequest = { onDismiss() },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = true,
                    indication = null,
                    onClick = { onDismiss() },
                    interactionSource = MutableInteractionSource()
                ), contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(250.dp)
                    .clickable(
                        enabled = true,
                        indication = null,
                        onClick = { },
                        interactionSource = MutableInteractionSource()
                    ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        if (dayPickerEnabled)
                            IconButton(onClick = { viewModel.plusDay() }, Modifier.weight(0.33f)) {
                                Icon(
                                    painter = rememberVectorPainter(image = Icons.Default.ExpandLess),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                            }
                        IconButton(onClick = { viewModel.plusMonth() }, Modifier.weight(0.33f)) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Default.ExpandLess),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { viewModel.plusYear() }, Modifier.weight(0.33f)) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Default.ExpandLess),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.Center) {
                        if (dayPickerEnabled)
                            Text(
                                text = date.dayOfMonth.toString(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.weight(0.33f)
                            )
                        Text(
                            text = date.month.displayText(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(0.33f)
                        )
                        Text(
                            text = date.year.toString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(0.33f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.Center) {
                        if (dayPickerEnabled)
                            IconButton(onClick = { viewModel.minusDay() }, Modifier.weight(0.33f)) {
                                Icon(
                                    painter = rememberVectorPainter(image = Icons.Default.ExpandMore),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                            }
                        IconButton(onClick = { viewModel.minusMonth() }, Modifier.weight(0.33f)) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Default.ExpandMore),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { viewModel.minusYear() }, Modifier.weight(0.33f)) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Default.ExpandMore),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        }
                    }
                    Row(Modifier.padding(top = 8.dp)) {
                        OutlinedButton(
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.weight(0.45f),
                            onClick = { onDismiss() }
                        ) { Text("Cancel") }
                        Spacer(modifier = Modifier.weight(0.1f))
                        Button(
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.weight(0.45f),
                            onClick = {
                                onDismiss()
                                onSelectDate(date)
                            }
                        ) { Text("OK") }
                    }
                }
            }

        }
    }
}
