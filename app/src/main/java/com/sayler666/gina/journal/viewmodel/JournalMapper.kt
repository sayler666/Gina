package com.sayler666.gina.journal.viewmodel

import com.sayler666.core.date.toLocalDate
import com.sayler666.core.html.getTextWithoutHtml
import com.sayler666.gina.db.Day
import com.sayler666.gina.journal.viewmodel.JournalState.DaysState
import com.sayler666.gina.journal.viewmodel.JournalState.EmptySearchState
import com.sayler666.gina.journal.viewmodel.JournalState.EmptyState
import mood.Mood
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DaysMapper @Inject constructor() {

    fun toJournalState(
        days: List<Day>,
        searchQuery: String,
        moods: List<Mood>
    ): JournalState {

        val daysResult = days.map {
            requireNotNull(it.id)
            requireNotNull(it.date)
            requireNotNull(it.content)
            val nonHtml = it.content.getTextWithoutHtml()
            DayEntity(
                id = it.id,
                dayOfMonth = getDayOfMonth(it.date),
                dayOfWeek = getDayOfWeek(it.date),
                yearAndMonth = getYearAndMonth(it.date),
                header = getYearAndMonth(it.date),
                shortContent = when (searchQuery.isNotEmpty()) {
                    true -> getShorContentAroundSearchQuery(nonHtml, searchQuery)
                    else -> getShortContent(nonHtml)
                },
                mood = it.mood
            )
        }

        return when {
            daysResult.isEmpty() && (searchQuery.isEmpty() && moods.containsAll(
                Mood.values().toList()
            )) -> EmptyState

            daysResult.isEmpty() && (searchQuery.isNotEmpty() || !moods.containsAll(
                Mood.values().toList()
            )) -> EmptySearchState

            daysResult.isNotEmpty() -> DaysState(daysResult, searchQuery)
            else -> EmptyState
        }
    }

    private fun getShorContentAroundSearchQuery(content: String, searchQuery: String): String {
        val searchQueryPosition = content.indexOf(searchQuery, ignoreCase = true)
        val before = searchQueryPosition - (shortContentMaxLength / 2 - searchQuery.length / 2)
        val after =
            searchQueryPosition + searchQuery.length + (shortContentMaxLength / 2 - searchQuery.length / 2)
        return content
            .substring(maxOf(0, before)..minOf(after, content.length - 1)).trimEnd()
            .let {
                if (content.length == it.length) return it
                var short = if (before > 0) "…".plus(it) else it
                short = if (after > content.length - 1) short else short.plus("…")
                short
            }
    }

    private fun getShortContent(content: String): String = content
        .substring(0..minOf(content.length - 1, shortContentMaxLength)).trimEnd()
        .let { if (content.length > it.length) it.plus("…") else it }

    private fun getDayOfMonth(timestamp: Long) = timestamp.toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd"))

    private fun getDayOfWeek(timestamp: Long) = timestamp.toLocalDate()
        .format(DateTimeFormatter.ofPattern("EEEE"))

    private fun getYearAndMonth(timestamp: Long) = timestamp.toLocalDate()
        .format(DateTimeFormatter.ofPattern("yyyy, MMMM"))

    companion object {
        private const val shortContentMaxLength = 120
    }
}

sealed class JournalState {
    object EmptyState : JournalState()
    object PermissionNeededState : JournalState()
    data class DaysState(
        val days: List<DayEntity> = emptyList(),
        val searchQuery: String? = null
    ) : JournalState()

    object EmptySearchState : JournalState()
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
