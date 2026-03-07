package com.sayler666.gina.friends.usecase

import com.sayler666.data.database.db.journal.JournalRepository
import com.sayler666.domain.model.journal.Friend
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetFriendUseCase {
    fun getFriendFlow(id: Int): Flow<Friend?>
}

class GetFriendUseCaseImpl @Inject constructor(
    private val journalRepository: JournalRepository
) : GetFriendUseCase {
    override fun getFriendFlow(id: Int): Flow<Friend?> = journalRepository.getFriendFlow(id)
}
