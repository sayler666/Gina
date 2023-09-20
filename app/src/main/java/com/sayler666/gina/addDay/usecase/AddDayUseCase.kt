package com.sayler666.gina.addDay.usecase

import android.database.SQLException
import androidx.room.withTransaction
import com.sayler666.gina.db.DayDetails
import com.sayler666.gina.db.DayFriends
import com.sayler666.gina.db.DaysDao
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.withDaysDao
import timber.log.Timber
import javax.inject.Inject

interface AddDayUseCase {
    suspend fun addDay(
        dayDetails: DayDetails
    )
}

class AddDayUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : AddDayUseCase {
    override suspend fun addDay(
        dayDetails: DayDetails
    ) {
        try {
            ginaDatabaseProvider.withDaysDao {
                ginaDatabaseProvider.getOpenedDb()?.withTransaction {
                    val dayId = addDay(dayDetails.day).toInt()
                    attachments(dayDetails, dayId)
                    friends(dayDetails, dayId)
                }
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }

    private suspend fun DaysDao.attachments(
        dayDetails: DayDetails,
        dayId: Int
    ) {
        dayDetails.attachments.toMutableList()
            .map { it.copy(dayId = dayId) }
            .let { insertAttachments(it) }
    }

    private suspend fun DaysDao.friends(
        dayDetails: DayDetails,
        dayId: Int
    ) {
        dayDetails.friends
            .map { friend -> DayFriends(dayId, friend.id) }
            .let { addFriendsToDay(it) }
    }
}
