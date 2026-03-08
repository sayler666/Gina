package com.sayler666.gina.dayDetails.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import javax.inject.Inject

interface GetNextPreviousIdDayUseCase {
    suspend fun getNextDayId(dayId: Int): Result<Int>
    suspend fun getPreviousDayId(dayId: Int): Result<Int>
}

class GetNextPreviousIdDayUseCaseUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : GetNextPreviousIdDayUseCase {
    override suspend fun getNextDayId(dayId: Int): Result<Int> =
        journalRepository.getNextDayId(dayId)

    override suspend fun getPreviousDayId(dayId: Int): Result<Int> =
        journalRepository.getPreviousDayId(dayId)
}
