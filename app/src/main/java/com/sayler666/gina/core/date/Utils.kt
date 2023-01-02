package com.sayler666.gina.core.date

import com.kizitonwose.calendar.core.CalendarMonth
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

fun DayOfWeek.displayText(uppercase: Boolean = false): String =
    getDisplayName(TextStyle.SHORT, Locale.getDefault()).let { value ->
        if (uppercase) value.uppercase(Locale.getDefault()) else value
    }

fun CalendarMonth.displayText(uppercase: Boolean = false): String =
    yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).let { value ->
        if (uppercase) value.uppercase(Locale.getDefault()) else value
    } + " ${yearMonth.year}"
