package com.sayler666.gina.dayDetailsEdit.usecase

import android.database.SQLException
import com.sayler666.gina.db.GinaDatabaseProvider
import com.sayler666.gina.db.entity.DayDetails
import com.sayler666.gina.db.withDaysDao
import timber.log.Timber
import javax.inject.Inject

interface DeleteDayUseCase {
    suspend fun deleteDay(dayDetails: DayDetails)
}

class DeleteDayUseCaseImpl @Inject constructor(
    private val ginaDatabaseProvider: GinaDatabaseProvider
) : DeleteDayUseCase {
    override suspend fun deleteDay(dayDetails: DayDetails) {
        try {
            ginaDatabaseProvider.withDaysDao {
                deleteDay(dayDetails.day)
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }
}
