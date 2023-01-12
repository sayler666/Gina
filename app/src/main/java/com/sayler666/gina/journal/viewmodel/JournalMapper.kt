package com.sayler666.gina.journal.viewmodel

import com.sayler666.gina.core.date.toLocalDate
import com.sayler666.gina.db.Day
import com.sayler666.gina.ui.Mood
import com.sayler666.gina.ui.Mood.Companion.mapToMoodOrNull
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
            shortContent = getShortContent(it.content),
            mood = it.mood.mapToMoodOrNull()
        )
    }

    private fun getShortContent(content: String): String = content
        .substring(0..minOf(content.length - 1, shortContentMaxLength)).trimEnd()
        .let {
            if (content.length > it.length) it.plus("…") else it
        }

    private fun getDayOfMonth(timestamp: Long) = timestamp.toLocalDate()
        .format(
            DateTimeFormatter.ofPattern("dd")
        )

    private fun getDayOfWeek(timestamp: Long) = timestamp.toLocalDate()
        .format(
            DateTimeFormatter.ofPattern("EEEE")
        )

    private fun getYearAndMonth(timestamp: Long) = timestamp.toLocalDate()
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
    val mood: Mood? = null
)
