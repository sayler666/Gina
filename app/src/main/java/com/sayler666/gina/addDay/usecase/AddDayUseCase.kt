package com.sayler666.gina.addDay.usecase

import android.database.SQLException
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.db.DayWithAttachment
import com.sayler666.gina.db.withDaysDao
import timber.log.Timber
import javax.inject.Inject

interface AddDayUseCase {
    suspend fun addDay(
        dayWithAttachment: DayWithAttachment
    )
}

class AddDayUseCaseImpl @Inject constructor(
    private val databaseProvider: DatabaseProvider
) : AddDayUseCase {
    override suspend fun addDay(
        dayWithAttachment: DayWithAttachment
    ) {
        try {
            databaseProvider.withDaysDao {
                val id = addDay(dayWithAttachment.day)
                dayWithAttachment.attachments.toMutableList()
                    .map { it.copy(dayId = id.toInt()) }
                    .let { insertAttachments(it) }
            }
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }
}
