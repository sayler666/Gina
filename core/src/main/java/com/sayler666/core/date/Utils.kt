package com.sayler666.core.date

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun LocalDate.toEpochMilliseconds() = toEpochDay() * MILLIS_IN_DAY

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochSecond(this / 1000)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L

fun getDayOfMonth(timestamp: Long): String = timestamp.toLocalDate()
    .format(
        DateTimeFormatter.ofPattern("dd")
    )

fun getDayOfWeek(timestamp: Long): String = timestamp.toLocalDate()
    .format(
        DateTimeFormatter.ofPattern("EEEE")
    )

fun getYearAndMonth(timestamp: Long): String = timestamp.toLocalDate()
    .format(
        DateTimeFormatter.ofPattern("yyyy, MMMM")
    )
