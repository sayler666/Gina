package com.sayler666.gina.feature.journal.viewmodel

import com.sayler666.gina.feature.journal.ui.DayRowState
import com.sayler666.gina.feature.journal.ui.HorizontalImagesCarouselState

sealed class JournalState {
    data class DaysState(
        val days: List<DayRowState> = emptyList(),
        val previousYearsAttachments: HorizontalImagesCarouselState = emptyList(),
        val incognitoMode: Boolean = false
    ) : JournalState()

    data object LoadingState : JournalState()
    data object EmptyState : JournalState()
    data object EmptySearchState : JournalState()
    data object PermissionNeededState : JournalState()
}
