package com.sayler666.gina.dayslist.viewmodel

import com.sayler666.gina.dayslist.viewmodel.Mood.NEUTRAL
import com.sayler666.gina.db.Day
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DaysMapper @Inject constructor() {
    fun mapToVm(days: List<Day>): List<DayEntity> = days.map {
        requireNotNull(it.id)
        requireNotNull(it.date)
        requireNotNull(it.content)
        DayEntity(
            id = it.id,
            dayOfMonth = getDayOfMonth(it.date),
            dayOfWeek = getDayOfWeek(it.date),
            yearAndMonth = getYearAndMonth(it.date),
            header = getYearAndMonth(it.date),
            shortContent = getShortContent(it.content)
        )
    }

    private fun getShortContent(content: String): String = content
        .substring(0..minOf(content.length - 1, shortContentMaxLength)).trimEnd()
        .let {
            if (content.length > it.length) it.plus("…") else it
        }

    private fun getDayOfMonth(timestamp: Long) = Instant.ofEpochSecond(timestamp / 1000)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(
            DateTimeFormatter.ofPattern("dd")
        )

    private fun getDayOfWeek(timestamp: Long) = Instant.ofEpochSecond(timestamp / 1000)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(
            DateTimeFormatter.ofPattern("EEEE")
        )

    private fun getYearAndMonth(timestamp: Long) = Instant.ofEpochSecond(timestamp / 1000)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(
            DateTimeFormatter.ofPattern("yyyy, MMMM")
        )

    companion object {
        private const val shortContentMaxLength = 120
    }
}

data class DayEntity(
    val id: Int,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val yearAndMonth: String,
    val header: String,
    val shortContent: String,
    val mood: Mood = NEUTRAL
)

enum class Mood {
    BAD, LOW, NEUTRAL, GOOD, SUPERB
}
