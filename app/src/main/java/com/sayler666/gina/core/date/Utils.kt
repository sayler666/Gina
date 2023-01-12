package com.sayler666.gina.core.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.core.CalendarMonth
import kotlinx.coroutines.flow.filterNotNull
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

fun DayOfWeek.displayText(uppercase: Boolean = false): String =
    getDisplayName(TextStyle.SHORT, Locale.getDefault()).let { value ->
        if (uppercase) value.uppercase(Locale.getDefault()) else value
    }

fun Month.displayText(uppercase: Boolean = false): String =
    getDisplayName(TextStyle.SHORT, Locale.getDefault()).let { value ->
        if (uppercase) value.uppercase(Locale.getDefault()) else value
    }

fun CalendarMonth.displayText(uppercase: Boolean = false): String =
    yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).let { value ->
        if (uppercase) value.uppercase(Locale.getDefault()) else value
    } + " ${yearMonth.year}"


private fun CalendarLayoutInfo.firstMostVisibleMonth(viewportPercent: Float = 50f): CalendarMonth? {
    return if (visibleMonthsInfo.isEmpty()) {
        null
    } else {
        val viewportSize = (viewportEndOffset + viewportStartOffset) * viewportPercent / 100f
        visibleMonthsInfo.firstOrNull { itemInfo ->
            if (itemInfo.offset < 0) {
                itemInfo.offset + itemInfo.size >= viewportSize
            } else {
                itemInfo.size - itemInfo.offset >= viewportSize
            }
        }?.month
    }
}

@Composable
fun rememberFirstMostVisibleMonth(
    state: CalendarState,
    viewportPercent: Float = 50f,
): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.firstMostVisibleMonth(viewportPercent) }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

fun LocalDate.toEpochMilliseconds() = toEpochDay() * MILLIS_IN_DAY

fun Long.ofEpochMilliseconds(): LocalDate =
    Instant.ofEpochSecond(this / 1000)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000
