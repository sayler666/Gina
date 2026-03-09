package com.sayler666.gina.feature.journal.viewmodel

import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.feature.journal.ui.DayRowState
import com.sayler666.gina.feature.journal.ui.HorizontalImagesCarouselState

sealed class JournalState(val filtersActive: Boolean = false) {
    data class DaysState(
        val days: List<DayRowState> = emptyList(),
        val searchQuery: String? = null,
        val previousYearsAttachments: HorizontalImagesCarouselState = emptyList(),
        val activeFilters: Boolean = false,
        val moods: List<Mood> = Mood.entries,
        val incognitoMode: Boolean = false
    ) : JournalState(activeFilters)

    data object LoadingState : JournalState()
    data class EmptyState(val activeFilters: Boolean = false) : JournalState(activeFilters)
    data class EmptySearchState(val activeFilters: Boolean = false) : JournalState(activeFilters)
    data object PermissionNeededState : JournalState()
}
