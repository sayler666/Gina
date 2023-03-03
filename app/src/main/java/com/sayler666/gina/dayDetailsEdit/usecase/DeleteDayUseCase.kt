package com.sayler666.gina.dayDetailsEdit.usecase

import android.database.SQLException
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.DayDetails
import com.sayler666.gina.db.withDaysDao
import timber.log.Timber
import javax.inject.Inject

interface DeleteDayUseCase {
    suspend fun deleteDay(
        dayDetails: DayDetails
    )
}

class DeleteDayUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider
) : DeleteDayUseCase {
    override suspend fun deleteDay(
        dayDetails: DayDetails
    ) {
        try {
            databaseProvider.withDaysDao {
                deleteDay(dayDetails.day)
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }
}
