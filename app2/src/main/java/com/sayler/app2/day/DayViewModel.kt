package com.sayler.app2.day

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.sayler.data.days.entity.Day

data class DayState(
        val days: Async<Day>
) : MvRxState

