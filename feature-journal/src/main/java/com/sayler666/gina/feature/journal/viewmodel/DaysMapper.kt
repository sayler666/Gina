package com.sayler666.gina.feature.journal.viewmodel

import com.sayler666.core.collections.pmap
import com.sayler666.core.date.getDayOfMonth
import com.sayler666.core.date.getDayOfWeek
import com.sayler666.core.date.getYearAndMonth
import com.sayler666.core.string.getTextWithoutHtml
import com.sayler666.domain.model.journal.AttachmentWithDay
import com.sayler666.domain.model.journal.Day
import com.sayler666.gina.attachments.ui.AttachmentState
import com.sayler666.gina.day.attachments.viewmodel.toState
import com.sayler666.gina.feature.journal.ui.DayRowState
import com.sayler666.gina.feature.journal.ui.HorizontalImagesCarouselState
import com.sayler666.gina.feature.journal.ui.ImageAttachmentState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.DaysState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.EmptySearchState
import com.sayler666.gina.feature.journal.viewmodel.JournalState.EmptyState
import java.time.LocalDate
import javax.inject.Inject

class DaysMapper @Inject constructor() {
    suspend fun toJournalState(
        days: List<Day>,
        searchQuery: String,
        filtersActive: Boolean,
        previousYearsAttachments: List<AttachmentWithDay>,
        incognitoMode: Boolean = false
    ): JournalState {

        val daysResult = days.pmap {
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
            daysResult.isEmpty() && !filtersActive && searchQuery.isEmpty() -> EmptyState
            daysResult.isEmpty() -> EmptySearchState
            else -> DaysState(
                days = daysResult,
                previousYearsAttachments = previousYearsAttachments.toPreviousYearsAttachments(),
                incognitoMode = incognitoMode
            )
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
                attachmentWithDay.day.date.let {
                    val yearsAgo = now.minusYears(it.year.toLong()).year
                    when (val attachmentState = attachmentWithDay.attachment.toState()) {
                        is AttachmentState.AttachmentImageState -> ImageAttachmentState(
                            attachmentState,
                            yearsAgo
                        )

                        else -> null
                    }
                }
            }
    }
}
