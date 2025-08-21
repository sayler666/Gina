package com.sayler666.domain.model.journal

import java.time.LocalDate

data class MoodAverage(
    val period: LocalDate,
    val moodAvg: Float
)
