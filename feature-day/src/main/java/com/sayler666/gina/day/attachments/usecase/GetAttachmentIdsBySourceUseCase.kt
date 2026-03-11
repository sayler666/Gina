package com.sayler666.gina.day.attachments.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.gina.navigation.routes.ImagePreviewSource
import javax.inject.Inject

interface GetAttachmentIdsBySourceUseCase {
    suspend fun getIds(source: ImagePreviewSource): List<Int>
}

class GetAttachmentIdsBySourceUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : GetAttachmentIdsBySourceUseCase {
    override suspend fun getIds(source: ImagePreviewSource): List<Int> = when (source) {
        ImagePreviewSource.Gallery -> journalRepository.getAllImageAttachmentIds()
        is ImagePreviewSource.Day -> source.attachmentIds
        is ImagePreviewSource.Journal -> source.attachmentIds
    }
}
