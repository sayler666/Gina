package com.sayler666.gina.feature.journal.viewmodel

import com.sayler666.gina.feature.journal.ui.DayRowState
import com.sayler666.gina.feature.journal.ui.HorizontalImagesCarouselState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed class JournalState {
    data class DaysState(
        val days: ImmutableList<DayRowState> = persistentListOf(),
        val previousYearsAttachments: HorizontalImagesCarouselState = persistentListOf(),
    ) : JournalState()

    data object LoadingState : JournalState()
    data object EmptyState : JournalState()
    data object EmptySearchState : JournalState()
}
