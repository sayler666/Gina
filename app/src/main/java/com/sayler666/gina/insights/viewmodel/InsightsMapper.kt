package com.sayler666.gina.insights.viewmodel

import androidx.compose.ui.graphics.Color
import com.sayler666.gina.core.date.toLocalDate
import com.sayler666.gina.core.list.mutate
import com.sayler666.gina.db.Day
import com.sayler666.gina.insights.viewmodel.ContributionLevel.Five
import com.sayler666.gina.insights.viewmodel.ContributionLevel.Four
import com.sayler666.gina.insights.viewmodel.ContributionLevel.One
import com.sayler666.gina.insights.viewmodel.ContributionLevel.Three
import com.sayler666.gina.insights.viewmodel.ContributionLevel.Two
import com.sayler666.gina.ui.Mood
import com.sayler666.gina.ui.Mood.BAD
import com.sayler666.gina.ui.Mood.Companion.mapToMoodOrNull
import com.sayler666.gina.ui.Mood.GOOD
import com.sayler666.gina.ui.Mood.LOW
import com.sayler666.gina.ui.Mood.NEUTRAL
import com.sayler666.gina.ui.Mood.SUPERB
import com.sayler666.gina.ui.mapToMoodIcon
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class InsightsMapper @Inject constructor() {
    fun toInsightsState(days: List<Day>): InsightsState {
        return InsightsState(
            totalEntries = days.size,
            currentStreak = calculateCurrentStreak(days),
            longestStreak = calculateLongestStreak(days),
            totalMoods = days.count { it.mood != null },
            contributionHeatMapData = generateContributionHeatMapData(days),
            moodHeatMapData = generateMoodHeatMapData(days),
            moodChartData = generateMoodChartData(days)
        )
    }

    private fun generateMoodChartData(days: List<Day>): List<MoodChartData> {
        val moods = days.mapNotNull { it.mood.mapToMoodOrNull() }

        val superbCount = moods.count { it == SUPERB }.toFloat()
        val goodCount = moods.count { it == GOOD }.toFloat()
        val neutralCount = moods.count { it == NEUTRAL }.toFloat()
        val lowCount = moods.count { it == LOW }.toFloat()
        val badCount = moods.count { it == BAD }.toFloat()

        return listOf<MoodChartData>().mutate {
            if (superbCount > 0) it.add(MoodChartData(superbCount, SUPERB))
            if (goodCount > 0) it.add(MoodChartData(goodCount, GOOD))
            if (neutralCount > 0) it.add(MoodChartData(neutralCount, NEUTRAL))
            if (lowCount > 0) it.add(MoodChartData(lowCount, LOW))
            if (badCount > 0) it.add(MoodChartData(badCount, BAD))
        }
    }

    private fun generateContributionHeatMapData(days: List<Day>): Map<LocalDate, ContributionLevel> {
        val heatMap = mutableMapOf<LocalDate, ContributionLevel>()

        val median = days.mapNotNull { it.content }.map { it.length }.sortedBy { it }.med()

        val bucket1 = 0..2 * median / 3
        val bucket2 = bucket1.last + 1..bucket1.last * 2
        val bucket3 = bucket2.last + 1..bucket1.last * 3
        val bucket4 = bucket3.last + 1..bucket1.last * 4

        Timber.d("Contributions buckets: $bucket1, $bucket2, $bucket3, $bucket4")

        days.forEach {
            requireNotNull(it.date)
            requireNotNull(it.content)
            val level = when (it.content.length) {
                in bucket1 -> One
                in bucket2 -> Two
                in bucket3 -> Three
                in bucket4 -> Four
                else -> Five
            }
            heatMap[it.date.toLocalDate()] = level
        }
        return heatMap
    }

    private fun List<Int>.med() = sorted().let {
        if (it.isEmpty()) return 0
        if (it.size % 2 == 0)
            (it[it.size / 2] + it[(it.size - 1) / 2]) / 2
        else
            it[it.size / 2]
    }

    private fun generateMoodHeatMapData(days: List<Day>): Map<LocalDate, Level> {
        val heatMap = mutableMapOf<LocalDate, Level>()
        days.forEach {
            requireNotNull(it.date)
            requireNotNull(it.content)
            val currentDayDate = it.date.toLocalDate()
            heatMap[currentDayDate] = when (it.mood) {
                -2 -> MoodLevel.One
                -1 -> MoodLevel.Two
                0 -> MoodLevel.Three
                1 -> MoodLevel.Four
                2 -> MoodLevel.Five
                else -> Zero
            }
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
    val contributionHeatMapData: Map<LocalDate, Level>,
    val moodHeatMapData: Map<LocalDate, Level>,
    val moodChartData: List<MoodChartData>
)

data class InsightsSearchState(
    val days: InsightsState? = null,
    val searchQuery: String? = null
) {
    fun hasResults() = days?.contributionHeatMapData?.isNotEmpty() == true && searchQuery != null
    fun emptyResults() = days?.contributionHeatMapData?.isEmpty() == true && searchQuery != null
    fun noSearch() = days?.contributionHeatMapData?.isEmpty() == true && searchQuery == null
}

interface Level {
    val color: Color
}

object Zero : Level {
    override val color: Color = Color(0xFF333836)
}

data class MoodChartData(
    val value: Float,
    val mood: Mood
)

enum class MoodLevel(override val color: Color) : Level {
    One(BAD.mapToMoodIcon().color),
    Two(LOW.mapToMoodIcon().color),
    Three(NEUTRAL.mapToMoodIcon().color),
    Four(GOOD.mapToMoodIcon().color),
    Five(SUPERB.mapToMoodIcon().color)
}

enum class ContributionLevel(override val color: Color) : Level {
    One(Color(0xFF0A4640)),
    Two(Color(0xFF0B6158)),
    Three(Color(0xFF279186)),
    Four(Color(0xFF37CEC1)),
    Five(Color(0xFF4FFCE8)),
}
