package com.sayler666.data.database.db.journal.entity

import androidx.room.ColumnInfo
import com.sayler666.domain.model.journal.Mood
import java.time.LocalDate

data class AttachmentIdWithDate(
    @ColumnInfo(name = "attachment_id") val id: Int,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "mood") val mood: Mood,
    @ColumnInfo(name = "content") val content: String,
)
