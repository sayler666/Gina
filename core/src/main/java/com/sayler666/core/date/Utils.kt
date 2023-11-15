package com.sayler666.core.date

import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L
const val SECONDS_IN_DAY = 24 * 60 * 60L

fun getDayOfMonth(localDate: LocalDate): String =
    localDate.format(DateTimeFormatter.ofPattern("dd"))

fun getDayOfWeek(localDate: LocalDate): String =
    localDate.format(DateTimeFormatter.ofPattern("EEEE"))

fun getYearAndMonth(localDate: LocalDate): String =
    localDate.format(DateTimeFormatter.ofPattern("yyyy, MMMM"))
