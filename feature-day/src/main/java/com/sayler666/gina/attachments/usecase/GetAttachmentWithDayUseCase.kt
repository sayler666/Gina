package com.sayler666.gina.attachments.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.AttachmentWithDay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetAttachmentWithDayUseCase {
    fun getAttachmentWithDayFlow(attachmentId: Int): Flow<AttachmentWithDay?>
}

class GetAttachmentWithDayUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : GetAttachmentWithDayUseCase {
    override fun getAttachmentWithDayFlow(attachmentId: Int) =
        journalRepository.getAttachmentWithDayFlow(attachmentId)
}
