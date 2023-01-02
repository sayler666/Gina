package com.sayler666.gina.calendar.viewmodel

import com.sayler666.gina.db.Day
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class CalendarMapper @Inject constructor() {
    fun mapToVm(days: List<Day>): List<CalendarDayEntity> = days.map {
        requireNotNull(it.id)
        requireNotNull(it.date)
        CalendarDayEntity(
            id = it.id,
            date = getDate(it.date)
        )
    }

    private fun getDate(date: Long): LocalDate = Instant.ofEpochSecond(date / 1000)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

data class CalendarDayEntity(
    val id: Int,
    val date: LocalDate
)
