package com.sayler666.gina.journal.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.AttachmentWithDay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PreviousYearsAttachmentsUseCase {
    operator fun invoke(): Flow<List<AttachmentWithDay>>
}

class PreviousYearsAttachmentsUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : PreviousYearsAttachmentsUseCase {
    override operator fun invoke(): Flow<List<AttachmentWithDay>> =
        journalRepository.previousYearsAttachments()

}
