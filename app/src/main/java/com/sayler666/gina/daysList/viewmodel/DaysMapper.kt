package com.sayler666.gina.daysList.viewmodel

import com.sayler666.gina.daysList.viewmodel.Mood.Companion.mapToMoodOrNull
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
            shortContent = getShortContent(it.content),
            mood = it.mood.mapToMoodOrNull()
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
    val mood: Mood? = null
)

enum class Mood(val numberValue: Int) {

    BAD(-2),
    LOW(-1),
    NEUTRAL(0),
    GOOD(1),
    SUPERB(2);

    companion object {
        fun Int?.mapToMood() = when (this) {
            -2 -> BAD
            -1 -> LOW
            0 -> NEUTRAL
            1 -> GOOD
            2 -> SUPERB
            else -> NEUTRAL
        }

        fun Int?.mapToMoodOrNull() = when (this) {
            -2 -> BAD
            -1 -> LOW
            0 -> NEUTRAL
            1 -> GOOD
            2 -> SUPERB
            else -> null
        }
    }
}

