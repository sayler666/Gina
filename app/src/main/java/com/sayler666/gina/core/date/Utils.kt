package com.sayler666.gina.core.date

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun LocalDate.toEpochMilliseconds() = toEpochDay() * MILLIS_IN_DAY

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochSecond(this / 1000)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000
