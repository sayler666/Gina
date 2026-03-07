package com.sayler666.data.database.db.journal.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.Day
import com.sayler666.domain.model.journal.Mood
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetDaysUseCase {
    fun getAllDaysFlow(): Flow<List<Day>>
    fun getFilteredDaysFlow(searchQuery: String = "", moods: List<Mood>): Flow<List<Day>>
}

class GetDaysUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : GetDaysUseCase {
    override fun getAllDaysFlow(): Flow<List<Day>> = journalRepository.daysFlow()

    override fun getFilteredDaysFlow(searchQuery: String, moods: List<Mood>): Flow<List<Day>> =
        journalRepository.daysWithFiltersFlow(searchQuery, *moods.toTypedArray())
}
