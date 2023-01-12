package com.sayler666.gina.calendar.viewmodel

import com.sayler666.gina.core.date.toLocalDate
import com.sayler666.gina.db.Day
import java.time.LocalDate
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

    private fun getDate(date: Long): LocalDate = date.toLocalDate()
}

data class CalendarDayEntity(
    val id: Int,
    val date: LocalDate
)
