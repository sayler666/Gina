package com.sayler666.gina.journal.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.AttachmentWithDay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject


interface PreviousYearsAttachmentsUseCase {
    operator fun invoke(): Flow<List<AttachmentWithDay>>
}

@OptIn(ExperimentalCoroutinesApi::class)
class PreviousYearsAttachmentsUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : PreviousYearsAttachmentsUseCase {
    override operator fun invoke(): Flow<List<AttachmentWithDay>> = flow {
        while (true) {
            emit(LocalDate.now())
            delay(30_000)
        }
    }
        .distinctUntilChanged()
        .flatMapLatest { currentDate ->
            journalRepository.previousYearsAttachments(currentDate)
        }
}
