package com.sayler666.gina.dayDetailsEdit.usecase

import android.database.SQLException
import androidx.room.withTransaction
import com.sayler666.gina.db.Attachment
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.DayDetails
import com.sayler666.gina.db.DayFriends
import com.sayler666.gina.db.DaysDao
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
                    attachments(dayDetails, attachmentsToDelete)
                    friends(dayDetails)
                }
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }

    private suspend fun DaysDao.friends(dayDetails: DayDetails) {
        dayDetails.day.id?.let { dayId ->
            deleteFriendsForDay(dayDetails.day.id)
            val dayFriends = dayDetails.friends.map { friend ->
                DayFriends(dayId, friend.id)
            }
            if (dayDetails.friends.isNotEmpty()) addFriendsToDay(dayFriends)
        }
    }

    private suspend fun DaysDao.attachments(
        dayDetails: DayDetails,
        attachmentsToDelete: List<Attachment>
    ) {
        val attachmentsToAdd = dayDetails.attachments.toMutableList()
            .filter { it.dayId == null }
            .map { it.copy(dayId = dayDetails.day.id) }

        if (attachmentsToAdd.isNotEmpty()) insertAttachments(attachmentsToAdd)
        if (attachmentsToDelete.isNotEmpty()) removeAttachments(attachmentsToDelete)
    }
}
