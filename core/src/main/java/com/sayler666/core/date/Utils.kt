package com.sayler666.core.date

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L
const val SECONDS_IN_DAY = 24 * 60 * 60L

fun getDayOfMonth(localDate: LocalDate): String =
    localDate.format(DateTimeFormatter.ofPattern("dd"))

fun getDayOfWeek(localDate: LocalDate): String =
    localDate.format(DateTimeFormatter.ofPattern("EEEE"))

fun getYearAndMonth(localDate: LocalDate): String =
    localDate.format(DateTimeFormatter.ofPattern("yyyy, MMMM"))

fun LocalDate.toUtcMillis(): Long =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atOffset(ZoneOffset.UTC).toLocalDate()
