package com.sayler666.gina.friends.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.FriendWithCount
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetAllFriendsByRecentUseCase {
    operator fun invoke(): Flow<List<FriendWithCount>>
}

class GetAllFriendsByRecentUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository,
) : GetAllFriendsByRecentUseCase {
    override fun invoke(): Flow<List<FriendWithCount>> =
        journalRepository.getAllFriendsWithCountByRecentFlow()
}
