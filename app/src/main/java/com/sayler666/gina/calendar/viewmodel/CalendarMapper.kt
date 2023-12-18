package com.sayler666.gina.calendar.viewmodel

import com.sayler666.gina.db.entity.Day
import java.time.LocalDate
import javax.inject.Inject

class CalendarMapper @Inject constructor() {
    fun mapToVm(days: List<Day>): List<CalendarDayEntity> = days.map {
        requireNotNull(it.id)
        requireNotNull(it.date)
        CalendarDayEntity(
            id = it.id,
            date = it.date
        )
    }

}

data class CalendarDayEntity(
    val id: Int,
    val date: LocalDate
)
