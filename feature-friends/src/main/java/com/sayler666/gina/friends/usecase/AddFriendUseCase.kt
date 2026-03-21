package com.sayler666.gina.friends.usecase

import com.sayler666.data.database.db.journal.dao.DaysDao
import com.sayler666.data.database.db.journal.entity.FriendEntity
import javax.inject.Inject

interface AddFriendUseCase {
    suspend fun addFriend(name: String)
}

class AddFriendUseCaseImpl @Inject constructor(
    private val daysDao: DaysDao
) : AddFriendUseCase {
    override suspend fun addFriend(name: String) {
        if (name.isBlank()) return
        daysDao.addFriend(FriendEntity(name = name, avatar = null))
    }
}
