package com.sayler666.domain.model.journal

data class DayDetails(
    val day: Day,

    val attachments: List<Attachment>,

    val friends: List<Friend>
)
