package com.sayler666.gina.friends.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.Friend
import javax.inject.Inject

interface DeleteFriendUseCase {
    suspend fun deleteFriend(friend: Friend)
}

class DeleteFriendUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository
) : DeleteFriendUseCase {
    override suspend fun deleteFriend(friend: Friend) = journalRepository.deleteFriend(friend)
}
