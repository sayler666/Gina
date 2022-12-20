package com.sayler666.gina.dayDetailsEdit.usecase

import android.database.SQLException
import com.sayler666.gina.db.Attachment
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.DayWithAttachment
import com.sayler666.gina.db.withDaysDao
import timber.log.Timber
import javax.inject.Inject

interface EditDayUseCase {
    suspend fun updateDay(
        dayWithAttachment: DayWithAttachment,
        attachmentsToDelete: List<Attachment>
    )
}

class EditDayUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider
) : EditDayUseCase {
    override suspend fun updateDay(
        dayWithAttachment: DayWithAttachment,
        attachmentsToDelete: List<Attachment>
    ) {
        try {
            databaseProvider.withDaysDao {
                updateDay(dayWithAttachment.day)

                val attachmentsToAdd = dayWithAttachment.attachments.toMutableList()
                    .map { it.copy(dayId = dayWithAttachment.day.id) }

                if (attachmentsToAdd.isNotEmpty()) insertAttachments(attachmentsToAdd)
                if (attachmentsToDelete.isNotEmpty()) removeAttachments(attachmentsToDelete)
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }
}