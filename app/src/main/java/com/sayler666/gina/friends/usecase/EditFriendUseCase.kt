package com.sayler666.gina.friends.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.Friend
import javax.inject.Inject

interface EditFriendUseCase {
    suspend fun editFriend(friend: Friend)
}

class EditFriendUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository
) : EditFriendUseCase {
    override suspend fun editFriend(friend: Friend) = journalRepository.editFriend(friend)
}
