package com.sayler666.gina.dayDetails.usecaase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.DayDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetDayDetailsUseCase {
    fun getDayDetailsFlow(id: Int): Flow<DayDetails?>
    suspend fun getDayDetails(id: Int): Result<DayDetails>
}

class GetDayDetailsUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : GetDayDetailsUseCase {
    override fun getDayDetailsFlow(id: Int): Flow<DayDetails?> = journalRepository.daysFlow(id)

    override suspend fun getDayDetails(id: Int): Result<DayDetails> = runCatching {
        journalRepository.getDay(id) ?: throw IllegalStateException("No day with id: $id")
    }
}
