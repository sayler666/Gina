package com.sayler666.gina.dayDetailsEdit.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.DayDetails
import javax.inject.Inject

interface DeleteDayUseCase {
    suspend fun deleteDay(dayDetails: DayDetails)
}

class DeleteDayUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : DeleteDayUseCase {
    override suspend fun deleteDay(dayDetails: DayDetails) {
        journalRepository.deleteDay(dayDetails)
    }
}
