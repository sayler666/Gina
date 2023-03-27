package com.sayler666.gina.dayDetailsEdit.usecase

import android.database.SQLException
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.DayWithAttachment
import com.sayler666.gina.db.withDaysDao
import timber.log.Timber
import javax.inject.Inject

interface DeleteDayUseCase {
    suspend fun deleteDay(
        dayWithAttachment: DayWithAttachment
    )
}

class DeleteDayUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider
) : DeleteDayUseCase {
    override suspend fun deleteDay(
        dayWithAttachment: DayWithAttachment
    ) {
        try {
            databaseProvider.withDaysDao {
                deleteDay(dayWithAttachment.day)
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }
}
