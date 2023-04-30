package com.sayler666.gina.calendar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.CalendarDay
import com.sayler666.gina.ui.theme.RobotoSlabRegular
import java.time.DayOfWeek
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle.FULL
import java.util.Locale

@Composable
fun Day(
    day: CalendarDay,
    textColor: Color,
    isSelected: Boolean,
    hasEntry: Boolean,
    dotColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 4.dp, top = 2.dp)
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            style = TextStyle(
                fontFamily = RobotoSlabRegular,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 18.sp
            ),
            modifier = Modifier
                .padding(top = 0.dp)
                .clip(shape = RoundedCornerShape(size = 10.dp))
        )
        if (hasEntry) {
            Icon(
                painter = rememberVectorPainter(image = Filled.FiberManualRecord),
                tint = dotColor,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun WeekDaysHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline,
                text = dayOfWeek.displayText(),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

fun DayOfWeek.displayText(uppercase: Boolean = false): String =
    getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()).let { value ->
        if (uppercase) value.uppercase(Locale.getDefault()) else value
    }

fun Month.displayText(uppercase: Boolean = false): String =
    getDisplayName(FULL, Locale.getDefault()).let { value ->
        if (uppercase) value.uppercase(Locale.getDefault()) else value
    }

fun YearMonth.displayText(uppercase: Boolean = false): String =
    "${this.month.displayText(uppercase)} ${this.year}"
