package com.sayler666.gina.insights.viewmodel

import com.sayler666.core.collections.mutate
import com.sayler666.core.collections.pmap
import com.sayler666.core.date.toLocalDate
import com.sayler666.core.html.getTextWithoutHtml
import com.sayler666.gina.db.Day
import com.sayler666.gina.insights.viewmodel.InsightState.DataState
import com.sayler666.gina.insights.viewmodel.InsightState.EmptySearchState
import com.sayler666.gina.insights.viewmodel.InsightState.EmptyState
import mood.Mood
import mood.Mood.AWESOME
import mood.Mood.BAD
import mood.Mood.EMPTY
import mood.Mood.GOOD
import mood.Mood.LOW
import mood.Mood.NEUTRAL
import mood.Mood.SUPERB
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class InsightsMapper @Inject constructor() {
    suspend fun toInsightsState(
        days: List<Day>,
        searchQuery: String,
        moods: List<Mood>
    ): InsightState = when {
        days.isEmpty() && (searchQuery.isEmpty() && moods.containsAll(Mood.entries))
        -> EmptyState

        days.isEmpty() && (searchQuery.isNotEmpty() || !moods.containsAll(Mood.entries))
        -> EmptySearchState

        days.isNotEmpty() -> DataState(
            totalEntries = days.size,
            currentStreak = calculateCurrentStreak(days),
            longestStreak = calculateLongestStreak(days),
            totalMoods = days.count { it.mood != EMPTY },
            contributionHeatMapData = generateContributionHeatMapData(days),
            moodHeatMapData = generateMoodHeatMapData(days),
            moodChartData = generateMoodChartData(days)
        )

        else -> EmptyState
    }

    private fun generateMoodChartData(days: List<Day>): List<MoodChartData> {
        val moods = days.map { it.mood }

        val awesomeCount = moods.count { it == AWESOME }.toFloat()
        val superbCount = moods.count { it == SUPERB }.toFloat()
        val goodCount = moods.count { it == GOOD }.toFloat()
        val neutralCount = moods.count { it == NEUTRAL }.toFloat()
        val lowCount = moods.count { it == LOW }.toFloat()
        val badCount = moods.count { it == BAD }.toFloat()

        return listOf<MoodChartData>().mutate {
            if (awesomeCount > 0) it.add(MoodChartData(awesomeCount, AWESOME))
            if (superbCount > 0) it.add(MoodChartData(superbCount, SUPERB))
            if (goodCount > 0) it.add(MoodChartData(goodCount, GOOD))
            if (neutralCount > 0) it.add(MoodChartData(neutralCount, NEUTRAL))
            if (lowCount > 0) it.add(MoodChartData(lowCount, LOW))
            if (badCount > 0) it.add(MoodChartData(badCount, BAD))
        }
    }

    private suspend fun generateContributionHeatMapData(days: List<Day>): Map<LocalDate, ContributionLevel> {
        val heatMap = mutableMapOf<LocalDate, ContributionLevel>()

        val median = days.mapNotNull { it.content }
            .pmap { it.getTextWithoutHtml() }
            .map { it.length }
            .sortedBy { it }
            .med()

        val bucket1 = 0..2 * median / 3
        val bucket2 = bucket1.last + 1..bucket1.last * 2
        val bucket3 = bucket2.last + 1..bucket1.last * 3
        val bucket4 = bucket3.last + 1..bucket1.last * 4

        Timber.d("Contributions buckets: $bucket1, $bucket2, $bucket3, $bucket4")

        days.forEach {
            requireNotNull(it.date)
            requireNotNull(it.content)
            heatMap[it.date.toLocalDate()] = when (it.content.getTextWithoutHtml().length) {
                in bucket1 -> ContributionLevel.One
                in bucket2 -> ContributionLevel.Two
                in bucket3 -> ContributionLevel.Three
                in bucket4 -> ContributionLevel.Four
                else -> ContributionLevel.Five
            }
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

    private fun generateMoodHeatMapData(days: List<Day>): Map<LocalDate, MoodLevel> {
        val heatMap = mutableMapOf<LocalDate, MoodLevel>()
        days.forEach {
            requireNotNull(it.date)
            requireNotNull(it.content)
            val currentDayDate = it.date.toLocalDate()
            heatMap[currentDayDate] = when (it.mood) {
                BAD -> MoodLevel.Bad
                LOW -> MoodLevel.Low
                NEUTRAL -> MoodLevel.Neutral
                GOOD -> MoodLevel.Good
                SUPERB -> MoodLevel.Superb
                AWESOME -> MoodLevel.Awesome
                else -> MoodLevel.Zero
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
        }
            .reversed()
            .forEach { day ->
                val currentDayDate = day.date?.toLocalDate()
                    ?: throw java.lang.IllegalStateException("No date found in day ${day.id}")
                if (currentStreak == 0) {
                    currentStreakStartDay = currentDayDate
                    currentStreak++
                } else if (currentStreak > 0) {
                    if (currentDayDate == currentStreakStartDay.minusDays(currentStreak.toLong())) {
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

sealed class InsightState {
    object LoadingState : InsightState()
    object EmptyState : InsightState()
    data class DataState(
        val totalEntries: Int,
        val currentStreak: Int,
        val longestStreak: Int,
        val totalMoods: Int,
        val contributionHeatMapData: Map<LocalDate, ContributionLevel>,
        val moodHeatMapData: Map<LocalDate, MoodLevel>,
        val moodChartData: List<MoodChartData>,
        val searchQuery: String? = null
    ) : InsightState()

    object EmptySearchState : InsightState()
}

data class MoodChartData(
    val value: Float,
    val mood: Mood
)

interface Level

enum class ContributionLevel : Level {
    Zero,
    One,
    Two,
    Three,
    Four,
    Five,
}

enum class MoodLevel : Level {
    Zero,
    Bad,
    Low,
    Neutral,
    Good,
    Superb,
    Awesome,
}
