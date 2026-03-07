package com.sayler666.gina.addDay.usecase

import android.database.SQLException
import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.DayDetails
import timber.log.Timber
import javax.inject.Inject

interface AddDayUseCase {
    suspend fun addDay(dayDetails: DayDetails)
}

class AddDayUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : AddDayUseCase {
    override suspend fun addDay(dayDetails: DayDetails) {
        try {
            journalRepository.addDay(dayDetails)
        } catch (e: SQLException) {
            Timber.e(e, "Database error")
        }
    }
}
