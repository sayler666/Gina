package com.sayler666.domain.model.journal

import java.time.LocalDate

data class Day(
    val date: LocalDate,
    val content: String = "",
    val id: Int = -1,
    val mood: Mood = Mood.EMPTY,
)
