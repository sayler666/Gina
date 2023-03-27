package com.sayler666.gina.dayDetailsEdit.usecase

import android.database.SQLException
import androidx.room.withTransaction
import com.sayler666.gina.db.Attachment
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.DayDetails
import com.sayler666.gina.db.withDaysDao
import timber.log.Timber
import javax.inject.Inject

interface EditDayUseCase {
    suspend fun updateDay(
        dayDetails: DayDetails,
        attachmentsToDelete: List<Attachment>
    )
}

class EditDayUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider
) : EditDayUseCase {
    override suspend fun updateDay(
        dayDetails: DayDetails,
        attachmentsToDelete: List<Attachment>
    ) {
        try {
            databaseProvider.withDaysDao {
                databaseProvider.getOpenedDb()?.withTransaction {
                    updateDay(dayDetails.day)
                    val attachmentsToAdd = dayDetails.attachments.toMutableList()
                        .filter { it.dayId == null }
                        .map { it.copy(dayId = dayDetails.day.id) }

                    if (attachmentsToAdd.isNotEmpty()) insertAttachments(attachmentsToAdd)
                    if (attachmentsToDelete.isNotEmpty()) removeAttachments(attachmentsToDelete)
                }
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }
}
