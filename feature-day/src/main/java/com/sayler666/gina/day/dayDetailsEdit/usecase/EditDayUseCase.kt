package com.sayler666.gina.day.dayDetailsEdit.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.Attachment
import com.sayler666.domain.model.journal.DayDetails
import javax.inject.Inject

interface EditDayUseCase {
    suspend fun updateDay(
        dayDetails: DayDetails,
        attachmentsToDelete: List<Attachment>
    )
}

class EditDayUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : EditDayUseCase {
    override suspend fun updateDay(
        dayDetails: DayDetails,
        attachmentsToDelete: List<Attachment>
    ) {
        journalRepository.updateDay(dayDetails, attachmentsToDelete)
    }
}
