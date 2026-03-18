package com.sayler666.gina.ui.filters

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sayler666.core.date.toLocalDate
import com.sayler666.core.date.toUtcMillis
import com.sayler666.gina.resources.R
import com.sayler666.gina.ui.filters.DateRange.Custom
import com.sayler666.gina.ui.filters.DateRange.LastMonth
import com.sayler666.gina.ui.filters.DateRange.LastYear
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun DateRangeFilter(
    dateRange: DateRange?,
    onDateRangeChanged: (DateRange?) -> Unit,
) {
    var showCustomPicker by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(
            LastMonth to stringResource(R.string.filters_date_last_month),
            LastYear to stringResource(R.string.filters_date_last_year),
        ).forEach { (preset, label) ->
            FilterChip(
                selected = dateRange == preset,
                onClick = { onDateRangeChanged(if (dateRange == preset) null else preset) },
                label = { Text(label) },
                shape = RoundedCornerShape(8.dp),
            )
        }

        val customLabel = if (dateRange is Custom) {
            val fmt = DateTimeFormatter.ofPattern("d MMM")
            "${dateRange.from.format(fmt)} – ${dateRange.to.format(fmt)}"
        } else {
            stringResource(R.string.filters_date_custom_range)
        }
        FilterChip(
            selected = dateRange is Custom,
            onClick = {
                if (dateRange is Custom) onDateRangeChanged(null)
                else showCustomPicker = true
            },
            label = { Text(customLabel) },
            shape = RoundedCornerShape(8.dp),
        )
    }

    if (showCustomPicker) {
        CustomDateRangePickerDialog(
            initialRange = dateRange as? Custom,
            onDismiss = { showCustomPicker = false },
            onConfirm = { from, to ->
                onDateRangeChanged(Custom(from, to))
                showCustomPicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDateRangePickerDialog(
    initialRange: Custom?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, LocalDate) -> Unit,
) {
    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialRange?.from?.toUtcMillis(),
        initialSelectedEndDateMillis = initialRange?.to?.toUtcMillis(),
        selectableDates = PastOrPresentSelectableDates
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val from = state.selectedStartDateMillis?.toLocalDate()
                    val to = state.selectedEndDateMillis?.toLocalDate()
                    if (from != null && to != null) onConfirm(from, to)
                },
                enabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null,
            ) { Text(stringResource(R.string.filters_date_ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.filters_date_cancel)) }
        },
    ) {
        val formatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy") }
        DateRangePicker(
            state = state,
            modifier = Modifier.weight(1f),
            title = {
                Text(
                    text = stringResource(R.string.filters_date_select_dates),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            headline = {
                Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                    Text(
                        text = state.selectedStartDateMillis?.toLocalDate()?.format(formatter) ?: "—",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = state.selectedEndDateMillis?.toLocalDate()?.format(formatter) ?: "—",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val date = Instant.ofEpochMilli(utcTimeMillis).atOffset(ZoneOffset.UTC).toLocalDate()
        return !date.isAfter(LocalDate.now())
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year <= LocalDate.now().year
    }
}

