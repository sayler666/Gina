package com.sayler666.gina.dayDetailsEdit.usecase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.dao.DaysDao
import com.sayler666.gina.db.entity.Attachment
import com.sayler666.gina.db.entity.DayDetails
import com.sayler666.gina.db.entity.DayFriends
import com.sayler666.gina.db.transactionWithDaysDao
import timber.log.Timber
import javax.inject.Inject

interface EditDayUseCase {
    suspend fun updateDay(
        dayDetails: DayDetails,
        attachmentsToDelete: List<Attachment>
    )
}

class EditDayUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : EditDayUseCase {
    override suspend fun updateDay(
        dayDetails: DayDetails,
        attachmentsToDelete: List<Attachment>
    ) {
        try {
            ginaDatabaseProvider.transactionWithDaysDao {
                updateDay(dayDetails.day)
                updateAttachments(dayDetails, attachmentsToDelete)
                updateFriends(dayDetails)
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }

    private suspend fun DaysDao.updateAttachments(
        dayDetails: DayDetails,
        attachmentsToDelete: List<Attachment>
    ) {
        val attachmentsToAdd = dayDetails.attachments.toMutableList()
            .filter { it.dayId == null }
            .map { it.copy(dayId = dayDetails.day.id) }

        if (attachmentsToAdd.isNotEmpty()) insertAttachments(attachmentsToAdd)
        if (attachmentsToDelete.isNotEmpty()) removeAttachments(attachmentsToDelete)
    }

    private suspend fun DaysDao.updateFriends(dayDetails: DayDetails) {
        dayDetails.day.id?.let { dayId ->
            deleteFriendsForDay(dayDetails.day.id)
            val dayFriends = dayDetails.friends.map { friend ->
                DayFriends(dayId, friend.id)
            }
            if (dayDetails.friends.isNotEmpty()) addFriendsToDay(dayFriends)
        }
    }
}
