package com.sayler666.gina.friends.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.FriendWithCount
import com.sayler666.domain.model.journal.Mood
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

interface GetAllFriendsUseCase {
    fun getAllFriendsWithCount(): Flow<List<FriendWithCount>>
    suspend fun getAllFriendsWithCount(
        searchQuery: String,
        moods: List<Mood>,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): List<FriendWithCount>
}

class GetAllFriendsUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : GetAllFriendsUseCase {
    override fun getAllFriendsWithCount(): Flow<List<FriendWithCount>> =
        journalRepository.getAllFriendsWithCountFlow()

    override suspend fun getAllFriendsWithCount(
        searchQuery: String,
        moods: List<Mood>,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): List<FriendWithCount> =
        journalRepository.getAllFriendsWithCount(searchQuery, moods, dateFrom, dateTo)
}
