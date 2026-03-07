package com.sayler666.gina.dayDetails.ui

import com.sayler666.gina.dayDetails.ui.Way.NONE

data class DayDetailsScreenNavArgs(
    val dayId: Int,
    val way: Way = NONE
)
