package com.sayler666.data.database.db.journal.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import javax.inject.Inject

class UpdateAttachmentHiddenUseCase @Inject constructor(
    private val journalRepository: JournalRepository,
) {
    suspend operator fun invoke(id: Int, hidden: Boolean) =
        journalRepository.updateAttachmentHidden(id, hidden)
}
