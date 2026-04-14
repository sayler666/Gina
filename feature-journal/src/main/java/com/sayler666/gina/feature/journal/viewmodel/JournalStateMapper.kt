package com.sayler666.gina.feature.journal.viewmodel

import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.core.string.getTextWithoutHtml
import com.sayler666.core.string.scrambleText
import com.sayler666.domain.model.journal.AttachmentWithDay
import com.sayler666.domain.model.journal.Day
import com.sayler666.gina.attachments.ui.AttachmentState.AttachmentImageState
import com.sayler666.gina.day.attachments.viewmodel.toState
import com.sayler666.gina.feature.journal.ui.DayRowState
import com.sayler666.gina.feature.journal.ui.HorizontalImagesCarouselState
import com.sayler666.gina.feature.journal.ui.ImageAttachmentState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.DaysState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.EmptySearchState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.EmptyState
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDate
import javax.inject.Inject

class JournalStateMapper @Inject constructor() {
    fun toJournalState(
        days: List<Day>,
        searchQuery: String,
        filtersActive: Boolean,
        previousYearsAttachments: HorizontalImagesCarouselState,
        imageAttachmentIds: Map<Int, List<Int>> = emptyMap(),
        incognitoMode: Boolean = false
    ): JournalState {

        val daysResult = days.map { day ->
            val nonHtml = day.content.getTextWithoutHtml()
            val allAttachmentIds = (imageAttachmentIds[day.id] ?: emptyList()).toImmutableList()
            val displayAttachmentIds = if (allAttachmentIds.size >= 2) allAttachmentIds.shuffled()
                .take(4).toImmutableList() else allAttachmentIds

            DayRowState(
                id = day.id,
                dayOfMonth = getDayOfMonth(day.date),
                dayOfWeek = getDayOfWeek(day.date),
                yearAndMonth = getYearAndMonth(day.date),
                header = getYearAndMonth(day.date),
                contentPreview = when (searchQuery.isNotEmpty()) {
                    true -> getContentPreviewAroundSearchQuery(nonHtml, searchQuery)
                    else -> nonHtml
                }.let { if (incognitoMode) it.scrambleText() else it },
                searchQuery = searchQuery,
                mood = day.mood,
                displayAttachmentIds = displayAttachmentIds,
                allAttachmentIds = allAttachmentIds
            )
        }.toImmutableList()

        return when {
            daysResult.isEmpty() && !filtersActive && searchQuery.isEmpty() -> EmptyState
            daysResult.isEmpty() -> EmptySearchState
            else -> DaysState(
                days = daysResult,
                previousYearsAttachments = previousYearsAttachments,
            )
        }
    }

    private fun getContentPreviewAroundSearchQuery(content: String, searchQuery: String): String {
        val searchQueryPosition = content.indexOf(searchQuery, ignoreCase = true)
        val before = searchQueryPosition - (CONTENT_PREVIEW_MAX_LENGTH / 2 - searchQuery.length / 2)
        val after =
            searchQueryPosition + searchQuery.length + (CONTENT_PREVIEW_MAX_LENGTH / 2 - searchQuery.length / 2)
        return content
            .substring(maxOf(0, before)..minOf(after, content.length - 1)).trimEnd()
            .let {
                if (content.length == it.length) return it
                var short = if (before > 0) "…".plus(it) else it
                short = if (after > content.length - 1) short else short.plus("…")
                short
            }
    }

    fun mapPreviousYearsAttachments(attachments: List<AttachmentWithDay>): HorizontalImagesCarouselState =
        attachments.toPreviousYearsAttachments()

    private fun List<AttachmentWithDay>.toPreviousYearsAttachments(): HorizontalImagesCarouselState {
        val now = LocalDate.now()
        return sortedByDescending { it.day.date }
            .mapNotNull { attachmentWithDay ->
                attachmentWithDay.day.date.let {
                    val yearsAgo = now.minusYears(it.year.toLong()).year
                    when (val attachmentState = attachmentWithDay.attachment.toState()) {
                        is AttachmentImageState -> ImageAttachmentState(
                            state = attachmentState,
                            yearsAgo = yearsAgo
                        )

                        else -> null
                    }
                }
            }.toImmutableList()
    }

    companion object {
        private const val CONTENT_PREVIEW_MAX_LENGTH = 120
    }
}
