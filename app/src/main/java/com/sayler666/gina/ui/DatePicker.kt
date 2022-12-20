package com.sayler666.gina.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.sayler666.gina.dayDetails.viewmodel.DayDetailsMapper.Companion.DATE_PATTERN
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun DatePicker(dateTimestamp: Long, onDateChanged: (epochMilliseconds: Long) -> Unit) {
    val context = LocalContext.current

    val localDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(dateTimestamp / 1000),
        ZoneId.systemDefault()
    )
    val year = localDateTime.year
    val month = localDateTime.monthValue - 1
    val day = localDateTime.dayOfMonth

    val formattedDate =
        remember { mutableStateOf(localDateTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN))) }

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            val newLocalDateTime = LocalDateTime.of(mYear, mMonth + 1, mDayOfMonth, 0, 0)
            onDateChanged(newLocalDateTime.toEpochSecond(ZoneOffset.UTC) * 1000)
            formattedDate.value = newLocalDateTime
                .format(DateTimeFormatter.ofPattern(DATE_PATTERN))
        }, year, month, day
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            mDatePickerDialog.show()
        }) {
        Text(text = formattedDate.value)
        Icon(
            Filled.ArrowDropDown,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = null
        )
    }
}
