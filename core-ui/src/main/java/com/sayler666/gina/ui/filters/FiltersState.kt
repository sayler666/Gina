package com.sayler666.gina.ui.filters

import com.sayler666.domain.model.journal.Mood

data class FiltersState(
    val searchQuery: String = "",
    val searchVisible: Boolean = false,
    val moods: List<Mood> = Mood.entries,
    val dateRange: DateRange? = null,
) {
    val filtersActive: Boolean
        get() = moods.size < Mood.entries.size || dateRange != null
}
