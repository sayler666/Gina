package com.sayler666.gina.addDay.usecase

import android.database.SQLException
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.DayDetails
import com.sayler666.gina.db.withDaysDao
import timber.log.Timber
import javax.inject.Inject

interface AddDayUseCase {
    suspend fun addDay(
        dayDetails: DayDetails
    )
}

class AddDayUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider
) : AddDayUseCase {
    override suspend fun addDay(
        dayDetails: DayDetails
    ) {
        try {
            databaseProvider.withDaysDao {
                val id = addDay(dayDetails.day)
                dayDetails.attachments.toMutableList()
                    .map { it.copy(dayId = id.toInt()) }
                    .let { insertAttachments(it) }
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }
}
