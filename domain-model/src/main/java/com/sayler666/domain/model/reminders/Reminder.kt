package com.sayler666.domain.model.reminders

import java.time.LocalTime

data class Reminder(
    val id: Int? = null,
    val time: LocalTime
)
