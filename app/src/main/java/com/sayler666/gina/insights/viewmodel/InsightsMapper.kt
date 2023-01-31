package com.sayler666.gina.insights.viewmodel

import androidx.compose.ui.graphics.Color
import com.sayler666.gina.core.date.toLocalDate
import com.sayler666.gina.db.Day
import com.sayler666.gina.insights.viewmodel.Level.Five
import com.sayler666.gina.insights.viewmodel.Level.Four
import com.sayler666.gina.insights.viewmodel.Level.One
import com.sayler666.gina.insights.viewmodel.Level.Three
import com.sayler666.gina.insights.viewmodel.Level.Two
import java.time.LocalDate
import javax.inject.Inject

class InsightsMapper @Inject constructor() {
    fun toInsightsState(days: List<Day>): InsightsState {
        return InsightsState(
            totalEntries = days.size,
            currentStreak = calculateCurrentStreak(days),
            longestStreak = calculateLongestStreak(days),
            totalMoods = days.count { it.mood != null },
            daysHeatMapData = generateHeatMapData(days)
        )
    }

    private fun generateHeatMapData(days: List<Day>): Map<LocalDate, Level> {
        val heatMap = mutableMapOf<LocalDate, Level>()
        days.forEach {
            requireNotNull(it.date)
            requireNotNull(it.content)
            val currentDayDate = it.date.toLocalDate()
            val level = when (it.content.length) {
                in 1..200 -> One
                in 201..400 -> Two
                in 401..600 -> Three
                in 601..800 -> Four
                else -> Five
            }
            heatMap[currentDayDate] = level
        }
        return heatMap
    }

    private fun calculateCurrentStreak(days: List<Day>): Int {
        var currentStreak = 0
        val currentDay = if (days.any { it.date?.toLocalDate() == LocalDate.now() }) {
            LocalDate.now()
        } else {
            LocalDate.now().minusDays(1)
        }
        days.sortedWith { day1, day2 ->
            requireNotNull(day1.date)
            requireNotNull(day2.date)
            day1.date.compareTo(day2.date)
        }
            .reversed()
            .forEachIndexed { i, day ->
                when (day.date?.toLocalDate()) {
                    currentDay -> currentStreak++
                    currentDay.minusDays(i.toLong()) -> currentStreak++
                    else -> return@forEachIndexed
                }
            }

        return currentStreak
    }

    private fun calculateLongestStreak(days: List<Day>): Int {
        var longestStreak = 0
        var currentStreak = 0
        var currentStreakStartDay = LocalDate.now()
        days.sortedWith { day1, day2 ->
            requireNotNull(day1.date)
            requireNotNull(day2.date)
            day1.date.compareTo(day2.date)
        }.forEach { day ->
            val currentDayDate = day.date?.toLocalDate()
                ?: throw java.lang.IllegalStateException("No date found in day ${day.id}")
            if (currentStreak == 0) {
                currentStreakStartDay = currentDayDate
                currentStreak++
            } else if (currentStreak > 0) {
                if (currentDayDate == currentStreakStartDay.plusDays(currentStreak.toLong())) {
                    currentStreak++
                } else {
                    longestStreak =
                        if (longestStreak > currentStreak) longestStreak else currentStreak
                    currentStreakStartDay = currentDayDate
                    currentStreak = 1
                }
            }
        }

        return longestStreak
    }
}

data class InsightsState(
    val totalEntries: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalMoods: Int,
    val daysHeatMapData: Map<LocalDate, Level>
)

data class InsightsSearchState(
    val days: InsightsState? = null,
    val searchQuery: String? = null
) {
    fun hasResults() = days?.daysHeatMapData?.isNotEmpty() == true && searchQuery != null
    fun emptyResults() = days?.daysHeatMapData?.isEmpty() == true && searchQuery != null
    fun noSearch() = days?.daysHeatMapData?.isEmpty() == true && searchQuery == null
}

enum class Level(val color: Color) {
    Zero(Color(0xFF333836)),
    One(Color(0xFF0A4640)),
    Two(Color(0xFF0B6158)),
    Three(Color(0xFF279186)),
    Four(Color(0xFF37CEC1)),
    Five(Color(0xFF4FFCE8)),
}
