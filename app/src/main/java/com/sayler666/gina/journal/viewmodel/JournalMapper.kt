package com.sayler666.gina.journal.viewmodel

import com.sayler666.core.collections.pmap
import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.core.string.getTextWithoutHtml
import com.sayler666.gina.attachments.viewmodel.AttachmentEntity.Image
import com.sayler666.gina.attachments.viewmodel.AttachmentMapper
import com.sayler666.gina.db.entity.AttachmentWithDay
import com.sayler666.gina.db.entity.Day
import com.sayler666.gina.journal.ui.DayRowState
import com.sayler666.gina.journal.ui.HorizontalImagesCarouselState
import com.sayler666.gina.journal.ui.ImageAttachment
import com.sayler666.gina.journal.viewmodel.JournalState.DaysState
import com.sayler666.gina.journal.viewmodel.JournalState.EmptySearchState
import com.sayler666.gina.journal.viewmodel.JournalState.EmptyState
import com.sayler666.gina.mood.Mood
import java.time.LocalDate
import javax.inject.Inject

class DaysMapper @Inject constructor(
    private val attachmentMapper: AttachmentMapper
) {

    suspend fun toJournalState(
        days: List<Day>,
        searchQuery: String,
        moods: List<Mood>,
        previousYearsAttachments: List<AttachmentWithDay>
    ): JournalState {

        val daysResult = days.pmap {
            requireNotNull(it.id)
            requireNotNull(it.date)
            requireNotNull(it.content)
            val nonHtml = it.content.getTextWithoutHtml()
            DayRowState(
                id = it.id,
                dayOfMonth = getDayOfMonth(it.date),
                dayOfWeek = getDayOfWeek(it.date),
                yearAndMonth = getYearAndMonth(it.date),
                header = getYearAndMonth(it.date),
                shortContent = when (searchQuery.isNotEmpty()) {
                    true -> getShorContentAroundSearchQuery(nonHtml, searchQuery)
                    else -> getShortContent(nonHtml)
                },
                searchQuery = searchQuery,
                mood = it.mood
            )
        }

        return when {
            daysResult.isEmpty() && (searchQuery.isEmpty() && moods.containsAll(
                Mood.entries
            )) -> EmptyState(activeFilters = moods.size != Mood.entries.size)

            daysResult.isEmpty() && (searchQuery.isNotEmpty() || !moods.containsAll(
                Mood.entries
            )) -> EmptySearchState(activeFilters = moods.size != Mood.entries.size)

            daysResult.isNotEmpty() -> DaysState(
                days = daysResult,
                searchQuery = searchQuery,
                previousYearsAttachments = previousYearsAttachments.toPreviousYearsAttachments(),
                activeFilters = moods.size != Mood.entries.size,
                moods = moods
            )

            else -> EmptyState(activeFilters = moods.size != Mood.entries.size)
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

    companion object {
        private const val shortContentMaxLength = 120
    }

    private fun List<AttachmentWithDay>.toPreviousYearsAttachments(): HorizontalImagesCarouselState {
        val now = LocalDate.now()
        return sortedByDescending { it.day.date }
            .mapNotNull { attachmentWithDay ->
                attachmentWithDay.day.date?.let {
                    val yearsAgo = now.minusYears(it.year.toLong()).year
                    val attachmentEntity =
                        attachmentMapper.mapToAttachmentEntity(attachmentWithDay.attachment)
                    when (attachmentEntity) {
                        is Image -> ImageAttachment(attachmentEntity, yearsAgo)
                        else -> null
                    }
                }
            }
    }
}

