package com.sayler666.gina.insights.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.MoodAverage
import javax.inject.Inject

interface GetAvgMoodByMonthsUseCase {
    suspend operator fun invoke(): List<MoodAverage>
}

class GetAvgMoodByMonthsUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository
) : GetAvgMoodByMonthsUseCase {
    override suspend fun invoke(): List<MoodAverage> = journalRepository.getAvgMoodsByMonth()
}
