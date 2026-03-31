package com.sayler666.data.database.db.journal.entity

import androidx.room.ColumnInfo

data class DayIdWithAttachmentId(
    @ColumnInfo(name = "days_id") val dayId: Int,
    @ColumnInfo(name = "attachment_id") val attachmentId: Int,
)