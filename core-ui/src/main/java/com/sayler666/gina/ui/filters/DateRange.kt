package com.sayler666.gina.ui.filters

import com.sayler666.gina.ui.filters.DateRange.Custom
import com.sayler666.gina.ui.filters.DateRange.LastMonth
import com.sayler666.gina.ui.filters.DateRange.LastYear
import java.time.LocalDate

sealed interface DateRange {
    data object LastMonth : DateRange
    data object LastYear : DateRange
    data class Custom(val from: LocalDate, val to: LocalDate) : DateRange
}

fun DateRange.toDateBounds(): Pair<LocalDate, LocalDate> {
    val today = LocalDate.now()
    return when (this) {
        LastMonth -> today.minusMonths(1) to today
        LastYear -> today.minusYears(1) to today
        is Custom -> from to to
    }
}
