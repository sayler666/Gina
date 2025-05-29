package com.sayler666.data.database.db.journal.entity

import androidx.room.Embedded
import com.sayler666.data.database.db.journal.entity.AttachmentEntity.Companion.toModel
import com.sayler666.data.database.db.journal.entity.DayEntity.Companion.toModel
import com.sayler666.domain.model.journal.AttachmentWithDay

data class AttachmentWithDayEntity(
    @Embedded val attachment: AttachmentEntity,
    @Embedded val day: DayEntity
) {
    companion object {
        fun AttachmentWithDayEntity.toModel() = AttachmentWithDay(
            attachment = attachment.toModel(),
            day = day.toModel()
        )
    }
}
